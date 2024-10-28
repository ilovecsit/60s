package com.common.pojo.chatRoom;


import lombok.Data;

import java.time.LocalDateTime;

/**
 * 群与成员对应表*/
@Data
public class GroupMem {
    private int groupId;
    private int groupMemId;

    /**
     * 群成员加入时间*/
    private LocalDateTime groupMemJoinTime;

    /**
     * 群成员是否退出该群聊*/
    private boolean delete;
}
