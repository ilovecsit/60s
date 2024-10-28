package com.common.util;

import com.common.pojo.Result;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.init.ResourceReader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class EmailUtil {
    /**
     * 向指定邮箱发送消息
     *
     * @param to 目标邮箱
     * @param subject 主题
     * @param text 发送消息
     * @return boolean 是否正确发送
     */

    private static String verificationMessage;

    static {
        InputStream resource = ResourceReader.class.getResourceAsStream("/static/registerEmail.html");
        try {
            verificationMessage = new String(resource.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    ;

    public synchronized static boolean sendTextEmail(JavaMailSender mailSender, String from, String to, String subject, String text) {

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true); // true 表示发送HTML邮件
            mailSender.send(message);
        } catch (Exception e) {
//            可以考虑使用其它smtp服务
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static String getVerificationMessage(String verification, String optionType) {
        String temp = "";
        for (int i = 0; i < verification.length(); i++) {
            temp += "<button class=\"button\" >" + verification.charAt(i) + "</button>";
        }


        String emailMessage = new String(verificationMessage);
        emailMessage = emailMessage.replace("<button class=\"button\">X</button>", temp);
        emailMessage = emailMessage.replace("xxxxxx", verification);
        emailMessage = emailMessage.replace("OOOOOO", optionType);

        return emailMessage;
    }
}
