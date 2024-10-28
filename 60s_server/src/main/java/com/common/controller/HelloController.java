package com.common.controller;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @RequestMapping ("hello")
    public String hello(){
        return "hello 60s";
    }

    @Autowired
    private SensitiveWordBs sensitiveWordBs;
    @PostMapping("/text")
    public String text(String text){
       return  "hello"+sensitiveWordBs.replace(text);
    }
}
