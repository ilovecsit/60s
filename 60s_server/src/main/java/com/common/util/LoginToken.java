package com.common.util;

import java.util.HashMap;
import java.util.Map;

public final class LoginToken {
    private static final Map<Integer, String> tokenMap = new HashMap<>();

    // 防止外部实例化
    private LoginToken() {
    }

    /**
     * 更新或添加用户ID和对应的token信息
     *
     * @param userId 用户ID
     * @param token  用户的token信息
     */
    public static void updateToken(Integer userId, String token) {
        tokenMap.put(userId, token);
    }

    /**
     * 获取用户ID对应的token信息
     *
     * @param userId 用户ID
     * @return 用户的token信息，如果不存在则返回null
     */
    public static String getToken(Integer userId) {
        return tokenMap.get(userId);
    }

    /**
     * 移除用户ID对应的token信息
     *
     * @param userId 用户ID
     * @return 被移除的token信息，如果不存在则返回null
     */
    public static String removeToken(Integer userId) {
        return tokenMap.remove(userId);
    }

    /**
     * 检查用户ID是否存在
     *
     * @param userId 用户ID
     * @return 如果用户ID存在则返回true，否则返回false
     */
    public static boolean containsUserId(Integer userId) {
        return tokenMap.containsKey(userId);
    }

    /**
     * 验证并解析token，确认其合法性以及与内部存储的token是否一致
     *
     * @param token 待验证的token
     * @return 如果token合法且与内部存储的一致，返回true，否则返回false
     */
    public static boolean validateToken(String token) {

        try {
            Map<String, Object> map = JwtUtil.parseUserLoginToken(token);

            // 从token中获取用户ID
            Integer userId = (Integer) map.get("userId");


            // 检查用户ID对应的token是否与提供的token一致
            if (userId != null && containsUserId(userId) && token.equals(getToken(userId))) {
//                ThreadLocalUtil.set(map);
                return true;
            }

        } catch (Exception e) {
            // 捕获任何解析或验证过程中可能出现的异常
        }

        return false;
    }
}