package com.common.pojo.chatRoom;

import lombok.Data;

@Data
public class JoinCodes {
    private Integer groupId;

    /**
     * 私密群聊的验证码
     * */
    private String joinCodes;
}
