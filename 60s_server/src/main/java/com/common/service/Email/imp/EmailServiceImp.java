package com.common.service.Email.imp;

import com.common.mapper.email.EmailVisitMapper;
import com.common.mapper.user.UserMapper;
import com.common.pojo.Record.EmailVisitRecord;
import com.common.pojo.Result;
import com.common.pojo.User.UserMainInf;
import com.common.service.Email.EmailService;
import com.common.util.EmailUtil;
import com.common.util.JwtUtil;
import com.common.util.RandomStringUtil;
import com.common.util.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 邮箱 service 类
 */
@Service
public class EmailServiceImp implements EmailService {
    @Autowired
    private EmailVisitMapper emailVisitMapper;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String from;

    private final static int verificationLength = 4;

    @Override
    public Result emailVerification(String to) {
        Date today = new Date(System.currentTimeMillis());
        String ip = RequestUtil.getClientIp();
        EmailVisitRecord emailVisitRecord = this.emailVisitMapper.selectEmailVisitRecordByIp(ip);
        if (emailVisitRecord == null) {
            this.emailVisitMapper.insertEmailVisitRecordByIp(RequestUtil.getClientIp(), today);
            emailVisitRecord = this.emailVisitMapper.selectEmailVisitRecordByIp(RequestUtil.getClientIp());
        }
        if (emailVisitRecord.getLastVisitDate().toString().equals(today.toString())) {
            if (emailVisitRecord.getVisitTime() >= emailVisitRecord.getMaxVisitTime()) {
                return Result.error(403, "今日邮箱服务次数达到上限");
            }
        } else {
            emailVisitRecord.setVisitTime(0);
        }

//        1-1-1.检查邮箱是否已经被注册，如果已经被注册，则返回失败结果，不算访问次数
        UserMainInf userMainInf = userMapper.selectByUserEmail(to);
        if (userMainInf != null) {
            return Result.error(409, "该邮箱已经被注册!");
        }

        String verification = RandomStringUtil.generateRandomString(verificationLength);
        String emailMessage = EmailUtil.getVerificationMessage(verification, "账号注册");


        if (EmailUtil.sendTextEmail(mailSender, from, to, "验证信息", emailMessage)) {
            this.emailVisitMapper.updateEmailVisitByIp(ip, today, emailVisitRecord.getVisitTime() + 1);
            Map<String, Object> map = new HashMap<>();
            map.put("verification", verification);
            map.put("userEmail", to);
            return Result.success(200, "邮箱发送成功", JwtUtil.generateVerificationToken(map));
        }

        return Result.error(500, "发送验证码失败，服务器资源受限");
    }


}
