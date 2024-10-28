package com.common.service.User;

import com.common.pojo.Result;
import com.common.pojo.User.UserMainInf;

public interface UserService {

    Result register(String userEmail, String verificationCode, String password);

    UserMainInf selectByUserEmail(String userEmail);
    

    Result login(String userEmail, String password);

    UserMainInf selectByUserId(Integer userId);

    Result exit();

    Result userModify(UserMainInf userMainInf);
}
