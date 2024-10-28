package com.common.pojo.chatRoom;

import lombok.Data;

@Data
public class GroupUserInf {
    private Integer groupId;

    private Integer groupMemId;

    private String userNickName;

    private String userPhotoUrl;

    private Integer userKind;
}
