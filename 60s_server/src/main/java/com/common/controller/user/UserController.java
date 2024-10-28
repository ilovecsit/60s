package com.common.controller.user;

import com.common.pojo.Result;
import com.common.pojo.User.UserMainInf;
import com.common.service.User.UserService;
import com.common.util.ThreadLocalUtil;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;


    /**
     * 用户注册接口
     *
     * @param userEmail        必须是邮箱格式（163或者qq）
     * @param verificationCode 验证码 必须是6位数字
     * @param password 密码 6到20位的数字字母字符，包含一些特殊字符
     * @
     */
    @PostMapping("/register")
    public Result userRegister(@Pattern(regexp = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$") String userEmail, @Pattern(regexp = "^[A-Za-z0-9]{4}$") String verificationCode, @Pattern(regexp = "^[0-9A-Za-z!@#$%^&*?.,]{6,20}$") String password) {
        return this.userService.register(userEmail, verificationCode, password);
    }

    /**
     * 用户登录接口
     */
    @PostMapping("/login")
    public Result login( @Pattern(regexp = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$") String userEmail, @Pattern(regexp = "^[0-9A-Za-z]{6,20}$") String password) {
        return this.userService.login(userEmail, password);
    }

    /**
     * 用户退出应用接口
     */
    @GetMapping("/exit")
    public Result exit() {
        return this.userService.exit();
    }

    /**
     * 获取用户id
     */

    @GetMapping("/id")
    public Result userId(){
        return Result.success(200,"用户id", ThreadLocalUtil.get());
    }

    @PutMapping("/userModify")
    public Result userModify(@RequestBody UserMainInf userMainInf){
        return this.userService.userModify(userMainInf);
    }
}
