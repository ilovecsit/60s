package com.common.controller.email;

import com.common.pojo.Result;
import com.common.service.Email.EmailService;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Validated
@Controller
@ResponseBody
@RequestMapping("/email")
public class EmailController {
    @Autowired
    private EmailService emailService;

    /**
     * 获取验证码接口
     */
    @GetMapping("/emailVerification")
    public Result emailVerification(@Pattern(regexp = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$") String to) {
        return this.emailService.emailVerification(to);
    }
}
