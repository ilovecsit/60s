package com.common.controller.group;

import com.common.pojo.Result;
import com.common.pojo.chatRoom.GagUser;
import com.common.pojo.chatRoom.GroupInf;
import com.common.service.group.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@ResponseBody
@Controller
@RequestMapping("/group")
public class GroupController {
    @Autowired
    private GroupService groupService;

    /**
     * 创建群聊
     * post
     * 需要传入 用户实体，群聊基本信息实体
     */
    @PostMapping("/createGroup")
    public Result createGroup( @RequestBody GroupInf groupInf) {
        return this.groupService.createGroup(groupInf);
    }

    /**
     * 加入群聊
     * post
     * 需要传入 用户 id，和 群聊 id
     */
    @PostMapping("/groupJoin")
    public Result groupJoinUser(Integer groupId) {
        return this.groupService.groupJoinUser(groupId);
    }

    /**
     * 查询所有公共群聊*/
    @GetMapping("/allPublicGroup")
    public Result allPublicGroup(){
        return this.groupService.allPublicAndSystemGroup();
    }


    /**
     * 查询所有公共群聊
     * */
    @GetMapping("/allPublicGroupExcludeUser")
    public Result allPublicGroupExcludeUserId(){
        return this.groupService.allPublicGroupExcludeUserId();
    }

    /**
     * 查询所有已经加入的群聊*/
    @GetMapping("/allGroupIncludeUser")
    public Result allGroupJoin(){
        return this.groupService.allGroupJoin();
    }

    /**
     * 设置群公告
     * */
    @PutMapping("/groupAnnouncementSet")
    public Result groupAnnouncementSet(@RequestBody GroupInf groupInf){
        return this.groupService.groupAnnouncementSet(groupInf);
    }

    @GetMapping("/likeSelect")
    public Result likeSelect(String keyWord){
        return this.groupService.likeSelect(keyWord);
    }

    @PutMapping("/groupModify")
    public Result groupModify(@RequestBody GroupInf groupInf){
        return this.groupService.groupModify(groupInf);
    }

    @GetMapping("/selectGroupUserCreated")
    public Result groupMySelect(){
        return this.groupService.groupMySelect();
    }

    @PutMapping("/deleteGroup")
    public Result groupDelete(@RequestBody GroupInf groupInf){
        return this.groupService.groupDelete(groupInf.getGroupId());
    }

    @GetMapping("/getJoinCode")
    public Result codeGet(Integer groupId){
        return this.groupService.codeGet(groupId);
    }

    @PutMapping("/secretGroupJoin")
    public Result secretGroupJoin(Integer groupId,String joinCode){
        return this.groupService.secretGroupJoin(groupId,joinCode);
    }

    @PutMapping("/addToGagList")
    public Result gagListAdd(@RequestBody GagUser gagUser){
        return this.groupService.gagListAdd(gagUser.getGroupId(), gagUser.getUserId());
    }

    @PutMapping("/removeFromGagList")
    public Result gagListRemove(@RequestBody GagUser gagUser){
        return this.groupService.gagListRemove(gagUser.getGroupId(), gagUser.getUserId());
    }

    @GetMapping("/selectGroupMemInf")
    public Result groupMemInf(Integer groupId){
        return this.groupService.groupMemInf(groupId);
    }
}
