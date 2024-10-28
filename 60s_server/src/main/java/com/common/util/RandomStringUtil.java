package com.common.util;

import java.util.Random;

public class RandomStringUtil {
    private static Random random=new Random();
    private RandomStringUtil(){
    }
    public static String generateRandomNum(int length){
        StringBuilder num=new StringBuilder("");
        for (int i = 0; i < length; i++) {
            num.append(random.nextInt(10));
        }
        return num.toString();
    }
    private static final char[] chars={
            '0','1','2','3','4','5','6','7','8','9',
            'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
            'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'
    };

    public static String generateRandomString(int length){
        int charsLength=chars.length;
        StringBuilder num=new StringBuilder("");
        for (int i = 0; i < length; i++) {
            num.append(chars[random.nextInt(charsLength)]);
        }
        return num.toString();
    }
}
