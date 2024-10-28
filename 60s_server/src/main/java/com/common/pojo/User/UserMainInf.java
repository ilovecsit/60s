package com.common.pojo.User;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserMainInf {
    /**
     * 用户id*/
    private Integer userId;

    /**
     * 用户密码
     * 不会转为json数据被前端接收*/
    @JsonIgnore
    private String userPassword;

    @JsonIgnore
    /**
     * 用户邮箱*/
    private String userEmail;

    /**
     * 用户昵称*/
    private String userNickName;

    /**
     * 用户头像url*/
    private String userPhotoUrl;

    @JsonIgnore
    /**
     * 用户创建时间*/
    private LocalDateTime userCreateTime;

    @JsonIgnore
    /**
     * 用户最后更新用户信息时间*/
    private LocalDateTime userLastUpdateTime;

    @JsonIgnore
    /**
     * 用户最近一次登录时间*/
    private LocalDateTime userLastLoginTime;

    @JsonIgnore
    /**
     * 用户系统角色
     * 0：系统管理员
     * 1.普通用户*/
    private Integer userRole;
}
