package com.common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;
import java.util.Map;

public class JwtUtil {

    private static final String KEY = "60s_Token_Key";
    /**
     * 用户登录有效时长:7天
     * 单位：豪秒
     */
	public static final long loginEffectiveTime=7*24*60*60*1000;


    public static final String userLoginClaimKey="userLoginClaim";
	//生成用户登陆的token
    public static String genUserLoginToken(Map<String, Object> userLoginClaim) {
        return JWT.create()
                .withClaim(userLoginClaimKey, userLoginClaim)
                .withExpiresAt(new Date(System.currentTimeMillis() + loginEffectiveTime))
                .sign(Algorithm.HMAC256(KEY));
    }



	//解析用户登录token
    public static Map<String, Object> parseUserLoginToken(String token) {
        return JWT.require(Algorithm.HMAC256(KEY))
                .build()
                .verify(token)
                .getClaim(userLoginClaimKey)
                .asMap();
    }

    private final static String verificationClaimKey="verificationClaimKey";


    /**
     * 验证码有效时长:10
     * 单位：min
     */
    private final static long verificationEffectiveTime=10*60*1000;
    //生成验证码token
    public static String generateVerificationToken(Map<String, Object> verificationToken) {
        return JWT.create()
                .withClaim(verificationClaimKey, verificationToken)
                .withExpiresAt(new Date(System.currentTimeMillis() + verificationEffectiveTime))
                .sign(Algorithm.HMAC256(KEY));
    }

    //解析验证码token
    public static Map<String, Object> parseVerificationToken(String token) {
        return JWT.require(Algorithm.HMAC256(KEY))
                .build()
                .verify(token)
                .getClaim(verificationClaimKey)
                .asMap();
    }
}
