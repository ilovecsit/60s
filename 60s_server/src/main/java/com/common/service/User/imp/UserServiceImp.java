package com.common.service.User.imp;

import com.common.mapper.user.UserMapper;
import com.common.pojo.Result;
import com.common.pojo.User.UserMainInf;
import com.common.service.User.UserService;
import com.common.util.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service

public class UserServiceImp implements UserService {
    @Autowired
    private UserMapper userMapper;

    /**
     * 用户注册
     * 需要正确的邮箱与验证码，会用token来验证
     */
    @Override
    public Result register(String userEmail, String verificationCode,/**密码长度在6到20位*/String password) {
//        0.检测token与验证码和用户账号是否匹配是否匹配
//        0-1.解析token
        String token = RequestUtil.getHeaderAttribute("Authorization");

        if (token == null) {
            return Result.error(401, "非法访问");
        }


//        0-2.根据解析结果判断传递的信息是否匹配
        Map<String, Object> map = null;
        try {
            map = JwtUtil.parseVerificationToken(token);
        } catch (Exception e) {
            return Result.error(401, "非法访问");
        }

        if (!(userEmail.equals(map.get("userEmail")) && verificationCode.equals(map.get("verification")))) {
            return Result.error(400, "验证码不匹配");
        }

//        1.检测该账号有没有被注册
        UserMainInf userMainInf = this.selectByUserEmail(userEmail);
        if (userMainInf != null) {
            return Result.error(409, "该邮箱已经被注册");
        }

        String userNickName = getUserNickName();

//        3.注册用户
        this.userMapper.register(userEmail, userNickName, verificationCode, Md5Util.getMD5String(password));
        return Result.success(200, "用户注册成功!");
    }

    private String getUserNickName() {
        return "60s用户_" + System.currentTimeMillis() % 100000000;
    }

    @Override
    public UserMainInf selectByUserEmail(String userEmail) {
        return this.userMapper.selectByUserEmail(userEmail);
    }

    @Override
    public UserMainInf selectByUserId(Integer userId) {
        return this.userMapper.selectByUserId(userId);
    }

    @Override
    public Result exit() {
        Map map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");
        UserMainInf userMainInf = this.selectByUserId(userId);

        LoginToken.updateToken(userId, "exit");
        return Result.success(200, "退出成功!");
    }

    @Autowired
    private ValidString validString;

    /**
     * 修改用户信息
     * 通过token确认用户
     * 只修改用户 昵称 和 用户头像url
     * 检查用户昵称是否合法
     **/
    @Override
    public Result userModify(UserMainInf userMainInf) {
        if (!validString.isValid(userMainInf.getUserNickName())) {
            return Result.error(400, "非法昵称");
        }
        try {
            Integer userId = ThreadLocalUtil.get();
            userMainInf.setUserId(userId);
            this.userMapper.updateUserNameAndPhotoUrlById(userMainInf);
        } catch (Exception exception) {
            exception.printStackTrace();
            return Result.error(500, "由于未知原因，修改失败！");
        }
        return Result.success(200, "修改成功!");
    }


    /**
     * 登录时：需要判断是否是正确的账号密码
     * 登录成功时，需要返回一个token，并记录token
     */
    @Override
    public Result login(String userEmail, String password) {
        UserMainInf userMainInf = this.selectByUserEmail(userEmail);
        if (userMainInf == null || !Md5Util.getMD5String(password).equals(userMainInf.getUserPassword())) {
            return Result.error(401, "邮箱或者密码错误!");
        }
        Map<String, Object> loginTokenClaimMap = new HashMap<>();
        loginTokenClaimMap.put("userId", userMainInf.getUserId());
        String token = JwtUtil.genUserLoginToken(loginTokenClaimMap);
        LoginToken.updateToken(userMainInf.getUserId(), token);
        userMainInf.setUserLastLoginTime(LocalDateTime.now());
        this.userMapper.updateUserMainInf(userMainInf);
        return Result.success(200, "登录成功!", new LoginRes(token, userMainInf));
    }


    @Data
    class LoginRes {
        private String token;
        private UserMainInf userMainInf;

        LoginRes(String token, UserMainInf userMainInf) {
            this.token = token;
            this.userMainInf = userMainInf;
        }
    }
}
