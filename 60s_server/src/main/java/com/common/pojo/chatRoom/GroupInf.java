package com.common.pojo.chatRoom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GroupInf {
    /**
     * 群id*/
    private int groupId;

    /**
     * 群名
     */
    private String groupName;

    /**
     * 群创建者id*/
    private int groupCreatedId;

    /**
     * 群创建时间*/
    private LocalDateTime groupCreatedTime;

    /**
     * 群最大人数*/
    private int groupMaxMemNum;

    /**
     *群最大管理员人数 */
    private int groupManagerMaxNum;


    /**
     * 群类型*/
    private int groupKind;

    /**
     * 群聊人数*/
    private int groupMemNum;

    /**
     * 群聊头像*/
    private String groupAvatar;

    /**
     * 群公告*/
    private String groupAnnouncement;

    /**
     * 现在保留的组消息数量
     * */
    private Integer groupMesNumNow;
}
