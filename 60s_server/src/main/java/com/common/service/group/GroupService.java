package com.common.service.group;

import com.common.pojo.Result;
import com.common.pojo.User.UserMainInf;
import com.common.pojo.chatRoom.GroupInf;

public interface GroupService {
    /**
     * 普通用户创建群聊*/
    Result createGroup(GroupInf groupInf);

    /**
     * 用户加入普通群聊*/
    Result groupJoinUser(Integer groupId);


    Result allPublicAndSystemGroup();

    /**
     * 所有群聊信息，除了用户已经加入的之外*/
    Result allPublicGroupExcludeUserId();

    Result allGroupJoin();

    Result groupAnnouncementSet(GroupInf groupInf);

    Result likeSelect(String keyWord);

    Result groupModify(GroupInf groupInf);

    Result groupMySelect();

    Result groupDelete(int groupId);

    Result codeGet(Integer groupId);

    Result secretGroupJoin(Integer groupId, String joinCode);

    Result gagListAdd(Integer groupId, Integer gag_user_id);

    Result groupMemInf(Integer groupId);

    Result gagListRemove(Integer groupId, Integer userId);
}
