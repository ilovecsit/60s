package com.common.util;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class ValidString {
     @Autowired
     private SensitiveWordBs sensitiveWordBs;

    public  boolean isValid(String string) {
        return string.equals(sensitiveWordBs.replace(string));
    }

    public String validString(String string){
        return sensitiveWordBs.replace(string);
    }
}
