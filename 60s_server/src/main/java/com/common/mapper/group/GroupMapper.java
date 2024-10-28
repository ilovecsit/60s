package com.common.mapper.group;

import com.common.pojo.chatRoom.GroupInf;
import com.common.pojo.chatRoom.GroupMem;
import com.common.pojo.chatRoom.GroupUserInf;
import com.common.pojo.chatRoom.JoinCodes;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface GroupMapper {

    /**
     * 系统初始化时自动创建组
     */
    @Insert("INSERT INTO all_group_mes (groupName,groupCreatedId,groupCreatedTime,groupMaxMemNum,groupManagerMaxNum,groupAvatar,groupKind) " +
            "VALUES ( #{groupName}, #{groupCreatedId}, #{groupCreatedTime}, #{groupMaxMemNum}, #{groupManagerMaxNum},#{groupAvatar},#{groupKind})")
    void addAutoGroup(String groupName,
                      Integer groupCreatedId,
                      LocalDateTime groupCreatedTime,
                      Integer groupMaxMemNum,
                      Integer groupManagerMaxNum,
                      String groupAvatar,
                      Integer groupKind
    );

    /**
     * 创建普通的群聊
     */
    @Insert("INSERT INTO all_group_mes (groupName,groupCreatedId,groupCreatedTime,groupKind) " +
            "VALUES ( #{groupName}, #{groupCreatedId}, #{groupCreatedTime},#{groupKind});")
    void addCommonGroup(String groupName,
                        Integer groupCreatedId,
                        LocalDateTime groupCreatedTime,
                        Integer groupKind
    );

    /**
     * 将用户加入群聊
     */
    @Insert("insert into group_mem (groupId,groupMemId,groupMemJoinTime,userKind) " +
            "values (#{groupId},#{userId},now(),#{userKind})")
    void addUserToGroup(Integer userId, Integer groupId, Integer userKind);

    /**
     * 获取用户与群聊的关系
     */
    @Select("select * from group_mem where groupId = #{groupId} and groupMemId = #{userId}")
    GroupMem getGroupMem(Integer userId, Integer groupId);

    /**
     * 再一次恢复加入群聊关系
     */
    @Update("update group_mem set `delete` = false where groupId = #{groupId} and groupMemId = #{userId}")
    void reJoin(Integer userId, Integer groupId);

    @Select("select groupMemNum from all_group_mes where groupId = #{groupId}")
    GroupInf getGroupMemNumById(Integer groupId);

    /**
     * 群聊人数加1
     */
    @Update("update all_group_mes set groupMemNum = groupMemNum + 1 where groupId = #{groupId}")
    void setGroupMemAdd(Integer groupId);

    /**
     * 获取所有群聊信息
     */
    @Select("select * from all_group_mes")
    List<GroupInf> getAllGroupInf();

    /**
     * 查询用户创建的群聊信息
     */
    @Select("select * from all_group_mes where groupCreatedId=#{userId}")
    List<GroupInf> selectGroupByCreatedId(Integer userId);

    /**
     * 获取群聊总数
     */
    @Select("select count(groupId) from all_group_mes")
    int getGroupsNum();

    /**
     * 获取所有群聊的名字
     */
    @Select("select groupName from all_group_mes")
    List<String> getAllGroupName();

    /**
     * 根据群聊名获取群id
     */
    @Select("select groupId from all_group_mes where groupName=#{groupName}")
    Integer getGroupIdByName(String groupName);

    /**
     * 表示是群主
     */
    @Insert("insert into group_mem (groupMemId,groupId,userKind,groupMemJoinTime) values (#{userId},#{groupId},1,now())")
    void isGroupOwner(int userId, Integer groupId);

    /**
     * 通过群聊id获取群聊
     */
    @Select("select * from all_group_mes where groupId=#{groupId}")
    GroupInf getGroupById(Integer groupId);

    /**
     * 修改群公告*/
    @Update("update all_group_mes set groupAnnouncement=#{groupAnnouncement} where groupId=#{groupId} and groupCreatedId=#{userId}")
    void groupAnnouncementSet(Integer userId,Integer groupId, String groupAnnouncement);

    /**
     * 模糊查询*/
    @Select("select * from all_group_mes where groupName like concat('%',#{keyWord},'%')" +
            " and (groupKind=0 or groupKind=1)" )
    List<GroupInf> likeSelect(String keyWord);

    @Update("update all_group_mes set groupName=#{groupName},groupKind=#{groupKind},groupAvatar=#{groupAvatar} where groupId=#{groupId} and groupCreatedId=#{groupCreatedId}")
    void groupModify(GroupInf groupInf);

    /**
     * 查询所有用户创建的群聊*/
    @Select("select * from all_group_mes where groupCreatedId=#{userId}")
    List<GroupInf> groupMySelect(Integer userId);

    @Delete("DELETE FROM all_group_mes WHERE groupId = #{groupId} AND groupCreatedId = #{userId};")
    void groupDelete(Integer groupId, Integer userId);

    @Delete("DELETE from group_mem where groupId=#{groupId}")
    void groupMemDelete(Integer groupId);

    @Insert("insert into secret_group_codes (groupId,joinCodes)" +
            "values (#{groupId},#{joinCodes})")
    void generateJoinCode(Integer groupId, String joinCodes);

    @Delete("DELETE from secret_group_codes where groupId=#{groupId}")
    void groupJoinCodeDelete(int groupId);

    @Select("select * from all_group_mes where groupId=#{groupId}")
    GroupInf selectGroupByGroupId(int groupId);

    @Select("select * from secret_group_codes where groupId=#{groupId}")
    JoinCodes codeGet(Integer groupId);

    @Update("update secret_group_codes set joinCodes=#{newJoinCodes} where groupId=#{groupId}")
    void updateJoinCodes(String newJoinCodes, Integer groupId);

    @Update("update group_mem set userKind=3 where groupId=#{groupId} and groupMemId=#{gagUserId} and userKind=2")
    void gagUserAdd(Integer groupId, Integer gagUserId);

    @Select("SELECT a.groupId, a.groupMemId ,a.userKind,b.userPhotoUrl,b.userNickName " +
            "FROM group_mem AS a " +
            "INNER JOIN usermaininformation AS b " +
            "ON b.userId = a.groupMemId " +
            "where a.groupId=#{groupId}")
    List<GroupUserInf> groupUserInf(Integer groupId);

    @Update("update group_mem set userKind=2 where groupId=#{groupId} and groupMemId=#{gagUserId} and userKind=3")
    void gagUserRemove(Integer groupId, Integer gagUserId);
}
