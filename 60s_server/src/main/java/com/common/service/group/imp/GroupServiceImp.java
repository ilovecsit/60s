package com.common.service.group.imp;

import com.common.mapper.group.GroupMapper;
import com.common.pojo.Result;
import com.common.pojo.chatRoom.*;
import com.common.service.Socket.group.imp.GroupSocketServiceImp;
import com.common.service.SystemManager.SystemManagerService;
import com.common.service.chatRoomManagerMachine.GroupManagerMachine;
import com.common.service.dataSync.DataSyncService;
import com.common.service.group.GroupService;
import com.common.util.ThreadLocalUtil;
import com.common.util.ValidString;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class GroupServiceImp implements GroupService {
    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private DataSyncService dataSyncService;

    private Integer groupAddLock = 1;


    @Autowired
    private SensitiveWordBs sensitiveWordBs;
    @Autowired
    private GroupSocketServiceImp groupSocketServiceImp;

    /**
     * 普通用户创建群聊
     * 需要验证 群聊信息 的规范
     */
    @Override
    @Transactional
    public synchronized Result createGroup(GroupInf groupInf) {

        int userId = ThreadLocalUtil.get();

        /**
         * 判断群聊类型是否违法*/
        if (groupInf.getGroupKind() != 1 && groupInf.getGroupKind() != 2) {
            return Result.error(403, "非法创建群聊");
        }

        /**
         * 判断群聊的建立是否已经超过了上限*/
        if (this.groupMapper.getGroupsNum() >= 300) {
            return Result.error(403, "为了用户体验，服务器不能创建更多的群聊");
        }

        /**
         * 查询该用户是否创建了群聊，如果有了，不可以再创建群聊(因为一个用户只能创建3个群聊）
         * */
        if (this.groupMapper.selectGroupByCreatedId(userId).size() > 2) {
            return Result.error(403, "你不能创建更多的群聊");
        }


        /**
         * 检查群聊信息是否合法
         * （主要是验证名字是否合法）
         * */

        if (!groupInf.getGroupName().equals(sensitiveWordBs.replace(groupInf.getGroupName()))) {
            return Result.error(403, "群聊名不合法");
        }

        /**
         * 将新群聊信息放到数据库，与此同时更新数据库的群聊数量
         * 与此同时将该创建者加入群聊*/
        this.groupMapper.addCommonGroup(
                groupInf.getGroupName(),
                userId,
                LocalDateTime.now(),
                groupInf.getGroupKind()
        );

        Integer groupId = this.groupMapper.getGroupIdByName(groupInf.getGroupName());

        this.groupMapper.isGroupOwner(userId, groupId);

        groupInf = this.groupMapper.getGroupById(groupId);
        GroupEntity groupEntity = new GroupEntity();
        groupEntity.setGroupInf(groupInf);
        GroupManagerMachine.addGroup(groupEntity);

        if(groupInf.getGroupKind()==2){
            generateJoinCode(groupInf.getGroupId());
        }

        return Result.success(200, "创建群聊成功", groupInf);
    }

    private void generateJoinCode(Integer groupId) {
        Random random=new Random();
        StringBuilder joinCodes=new StringBuilder();
        for (int i = 0; i < 98; i++) {
            joinCodes.append(generateRandomString(random.nextInt(5)+5));
            joinCodes.append("|");
        }
        joinCodes.append(generateRandomString(random.nextInt(5)+5));
        this.groupMapper.generateJoinCode(groupId,joinCodes.toString());
    }

    private StringBuilder generateRandomString(int length){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int random = (int) (Math.random() * 62);
            char c = 0;
            if (random < 10) {
                c = (char) ('0' + random);
            } else if (random < 36) {
                c = (char) ('a' + random - 10);
            } else {
                c = (char) ('A' + random - 36);
            }
            sb.append(c);
        }
        return sb;
    }
    /**
     * 用户加入普通群聊
     */
    @Override
    public Result groupJoinUser(Integer groupId) {
        Integer userId = ThreadLocalUtil.get();
        /**
         * 如果该群聊为普通群聊且存在，尝试加入*/

        GroupInf groupInf = dataSyncService.getGroupInfMap().get(groupId);
        int groupKind = groupInf.getGroupKind();
        if (groupInf == null || (groupKind != 0 && groupKind != 1)) {
            return Result.error(403, "非法请求");
        }

        synchronized (groupAddLock) {
            int groupNumNow = this.groupMapper.getGroupMemNumById(groupId).getGroupMemNum();
            if (groupNumNow >= groupInf.getGroupMaxMemNum()) {
                return Result.error(403, "群聊人数已满");
            }
        }

        /**
         * 查询该 对应关系信息 存在则 解析 是否为 已经删除群聊 如果删除 则修改值 不存在 直接加入*/
        GroupMem groupMem = this.groupMapper.getGroupMem(userId, groupId);
        if (groupMem == null) {
            this.groupMapper.addUserToGroup(userId, groupId, 2);
        } else if (groupMem.isDelete()) {
            this.groupMapper.reJoin(userId, groupId);
        } else {
            return Result.error(403, "非法请求");
        }

        Integer lock = 1;
        synchronized (lock) {
            this.groupMapper.setGroupMemAdd(groupId);
        }
        return Result.success(200, "加入群聊成功");
    }

    @Override
    public Result allPublicAndSystemGroup() {
        List<GroupInf> resData = new LinkedList<>();
        for (Map.Entry<Integer, GroupInf> integerGroupInfEntry : dataSyncService.getGroupInfMap().entrySet()) {
            int groupKind = integerGroupInfEntry.getValue().getGroupKind();
            if (groupKind == 0 || groupKind == 1) {
                resData.add(integerGroupInfEntry.getValue());
            }
        }
        return Result.success(200, "请求成功", resData);
    }

    /**
     * 获取所有的公共群聊信息除了用户已经加入的群聊之外
     **/
    @Value("${groupSetting.autoPublicGroupNum}")
    public Integer autoPublicGroupNum;

    @Override
    public Result allPublicGroupExcludeUserId() {
        int count = 0;
        /**
         * 一次最多允许返回5个群聊信息*/
        int max_res = 5;


        List<GroupInf> resData = new LinkedList<>();
        Set<Integer> userJoined = new HashSet<>();
        Integer threadUserId = ThreadLocalUtil.get();
        for (GroupMem groupMem : dataSyncService.getGroupMem()) {
            if (groupMem.getGroupMemId() == threadUserId && groupMem.isDelete() == false) {
                userJoined.add(groupMem.getGroupId());
            }
        }
        for (Map.Entry<Integer, GroupInf> integerGroupInfEntry : dataSyncService.getGroupInfMap().entrySet()) {
            int groupKind = integerGroupInfEntry.getValue().getGroupKind();
            if (!userJoined.contains(integerGroupInfEntry.getValue().getGroupId()) && groupKind == 0) {
                resData.add(integerGroupInfEntry.getValue());
            }
        }
        for (Map.Entry<Integer, GroupInf> integerGroupInfEntry : dataSyncService.getGroupInfMap().entrySet()) {
            int groupKind = integerGroupInfEntry.getValue().getGroupKind();
            if (!userJoined.contains(integerGroupInfEntry.getValue().getGroupId()) && groupKind == 1) {
                resData.add(integerGroupInfEntry.getValue());
                if (++count >= max_res) {
                    break;
                }
            }
        }
        return Result.success(200, "请求成功", resData);
    }

    /**
     * 获取所有的公共群聊信息除了用户已经加入的群聊之外
     * 没有数据量限制
     **/
    public Result allPublicGroupExcludeUserIdNoLimit() {

        List<GroupInf> resData = new LinkedList<>();
        Set<Integer> userJoined = new HashSet<>();
        Integer threadUserId = ThreadLocalUtil.get();
        for (GroupMem groupMem : dataSyncService.getGroupMem()) {
            if (groupMem.getGroupMemId() == threadUserId && groupMem.isDelete() == false) {
                userJoined.add(groupMem.getGroupId());
            }
        }
        for (Map.Entry<Integer, GroupInf> integerGroupInfEntry : dataSyncService.getGroupInfMap().entrySet()) {
            int groupKind = integerGroupInfEntry.getValue().getGroupKind();
            if (!userJoined.contains(integerGroupInfEntry.getValue().getGroupId()) && (groupKind == 0 || groupKind == 1)) {
                resData.add(integerGroupInfEntry.getValue());
            }
        }
        return Result.success(200, "请求成功", resData);
    }

    @Override
    public Result allGroupJoin() {
        List<GroupInf> resData = new LinkedList<>();
        Set<Integer> userJoined = new HashSet<>();
        Integer threadUserId = ThreadLocalUtil.get();
        for (GroupMem groupMem : dataSyncService.getGroupMem()) {
            if (groupMem.getGroupMemId() == threadUserId && groupMem.isDelete() == false) {
                userJoined.add(groupMem.getGroupId());
            }
        }
        GroupInf groupInf ;
        for (Map.Entry<Integer, GroupInf> integerGroupInfEntry : dataSyncService.getGroupInfMap().entrySet()) {
            if (userJoined.contains(integerGroupInfEntry.getValue().getGroupId())) {
                groupInf= integerGroupInfEntry.getValue();
                groupInf.setGroupMesNumNow(GroupSocketServiceImp.groupMesNumMap.getOrDefault(groupInf.getGroupId(),0));
                resData.add(groupInf);
            }
        }
        return Result.success(200, "请求成功", resData);
    }

    @Autowired
    private ValidString validString;

    /**
     * 获取用户id，尝试更新
     */
    @Override
    public Result groupAnnouncementSet(GroupInf groupInf) {
        try {
            groupInf.setGroupAnnouncement(validString.validString(groupInf.getGroupAnnouncement()));
            Integer userId = ThreadLocalUtil.get();
            this.groupMapper.groupAnnouncementSet(userId, groupInf.getGroupId(), groupInf.getGroupAnnouncement());
            return Result.success(200, "设置群公告成功");
        } catch (Exception exception) {
            return Result.error(400, "请输入正确的信息");
        }
    }

    @Override
    public Result likeSelect(String keyWord) {
        if (keyWord == null || keyWord == "") {
            return Result.error(400, "非法字符串");
        }
        List<GroupInf> allData = (List<GroupInf>) allPublicGroupExcludeUserIdNoLimit().getData();
        List<GroupInf> resData = new LinkedList<>();
        for (GroupInf resDatum : allData) {
            if (resDatum.getGroupName().contains(keyWord)) {
                resData.add(resDatum);
            }
        }
        return Result.success(200, "查询成功", resData);
    }

    /**
     * 修改群聊信息
     */
    @Override
    public Result groupModify(GroupInf groupInf) {
        Integer userId = ThreadLocalUtil.get();
        GroupInf oldGroupInf=this.groupMapper.getGroupById(groupInf.getGroupId());
        if(userId!=oldGroupInf.getGroupCreatedId()){
            return Result.error(400,"非法修改");
        }
        try {
            groupInf.setGroupCreatedId(userId);
            this.groupMapper.groupModify(groupInf);
            if(groupInf.getGroupKind()==2&&oldGroupInf.getGroupKind()!=2){
                generateJoinCode(groupInf.getGroupId());
            }
            return Result.error(200, "信息修改成功");
        } catch (Exception exception) {
            return Result.error(500, "请检查信息是否输入正确");
        }
    }

    @Override
    public Result groupMySelect() {
        Integer userId = ThreadLocalUtil.get();
        List<GroupInf> resData = this.groupMapper.groupMySelect(userId);
        return Result.success(200, "查询成功", resData);
    }

    /**
     * 应该先判断权限
     * 然后再删除*/
    @Override
    @Transactional
    public Result groupDelete(int groupId) {
        Integer userId=ThreadLocalUtil.get();
        GroupInf groupInf=this.groupMapper.selectGroupByGroupId(groupId);
        if(groupInf.getGroupCreatedId()!=userId){
            return Result.error(400,"非法删除");
        }

        this.groupMapper.groupMemDelete(groupId);
        this.groupMapper.groupJoinCodeDelete(groupId);
        this.groupMapper.groupDelete(groupId, userId);
        dataSyncService.deleteGroup(groupId);
        groupSocketServiceImp.deleteGroup(groupId);
        return Result.success(200, "删除成功");
    }

    @Override
    public Result codeGet(Integer groupId) {
        Integer userId=ThreadLocalUtil.get();
        GroupInf groupInf=this.groupMapper.selectGroupByGroupId(groupId);
        if(groupInf.getGroupCreatedId()!=userId){
            return Result.error(400,"非法访问");
        }

        JoinCodes joinCodes=this.groupMapper.codeGet(groupId);
        return Result.success(200,"查询成功",joinCodes.getJoinCodes());
    }

    /**
     * 先判断加入码是否正确
     * 如果正确删除验证码
     * 然后加入
     * */
    @Override
    @Transactional
    public synchronized Result secretGroupJoin(Integer groupId, String joinCode) {
        if(isValidJoinCode(groupId,joinCode)){
            String newJoinCodes="";
            String[] joinCodesArray=joinCodes.split("\\|");
            for (int i = 0; i < joinCodesArray.length; i++) {
                if(!joinCodesArray[i].equals(joinCode)){
                    newJoinCodes+=joinCodesArray[i]+"|";
                }
            }
            if(newJoinCodes.charAt(newJoinCodes.length()-1)=='|'){
                newJoinCodes=newJoinCodes.substring(0,newJoinCodes.length()-1);
            }
            this.groupMapper.updateJoinCodes(newJoinCodes,groupId);

            this.groupMapper.addUserToGroup(ThreadLocalUtil.get(),groupId,2);
            return Result.success(200,"加入成功");
        }
        return Result.error(400,"请检查输入的信息是否正确");
    }

    @Autowired
    private SystemManagerService systemManagerService;
    /**
     * 先检查用户是否为群主*/
    @Override
    @Transactional
    public synchronized Result gagListAdd(Integer groupId, Integer gag_user_id) {
        Integer userId=ThreadLocalUtil.get();
        GroupInf groupInf=this.groupMapper.getGroupById(groupId);
        if(groupInf.getGroupCreatedId()!=userId||systemManagerService.getSystemManager().getUserId().equals(gag_user_id)){
            return Result.error(400,"没有访问权限");
        }
        this.groupMapper.gagUserAdd(groupId,gag_user_id);
        GroupSocketServiceImp.gagUserAdd(groupId,gag_user_id);
        return Result.success(200,"设置禁言成功");
    }

    /**
     * 先判断用户是否是群管理员*/
    @Override
    public Result groupMemInf(Integer groupId) {
        Integer userId=ThreadLocalUtil.get();
        GroupInf groupInf=this.groupMapper.getGroupById(groupId);
        if(groupInf.getGroupCreatedId()!=userId){
            return Result.error(400,"没有访问权限");
        }
        List<GroupUserInf> groupMemList=this.groupMapper.groupUserInf(groupId);
        return Result.success(200,"查询成功",groupMemList);
    }

    @Override
    public Result gagListRemove(Integer groupId, Integer gag_Id) {
        Integer userId=ThreadLocalUtil.get();
        GroupInf groupInf=this.groupMapper.getGroupById(groupId);
        if(groupInf.getGroupCreatedId()!=userId){
            return Result.error(400,"没有访问权限");
        }
        this.groupMapper.gagUserRemove(groupId,gag_Id);
        GroupSocketServiceImp.gagUserRemove(groupId,gag_Id);
        return Result.success(200,"设置禁言成功");
    }

    String joinCodes="";
    private boolean isValidJoinCode(Integer groupId, String joinCode) {
        joinCodes=this.groupMapper.codeGet(groupId).getJoinCodes();

        for (String s : joinCodes.split("\\|")) {
            if(s.equals(joinCode)){
                return true;
            }
        }
        return false;
    }


    /**
     * 判断是否 该 群聊 成员 关系 已经存在 如果存在，判断是否删除，如果没有删除，则返回true，否则，返回false
     */
    private boolean alreadyExitAndNoDelete(List<GroupMem> groupMems, Integer userId, Integer groupId) {
        for (GroupMem groupMem : groupMems) {
            if (groupMem.getGroupId() == groupId && groupMem.getGroupMemId() == userId && !groupMem.isDelete()) {
                return true;
            }
        }
        return false;
    }


}
