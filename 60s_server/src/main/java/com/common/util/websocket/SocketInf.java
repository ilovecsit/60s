package com.common.util.websocket;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SocketInf {

    private SocketInf() {
    }

    /**
     * 用来记录 已存在的 websocket 连接 ，key 为 用户id ，值为 用户登录授权
     */
    private static final Map<Integer, String> websocketLive = new ConcurrentHashMap<>();

    /**
     * 向表中插入一条数据
     */
    public synchronized static boolean insert(Integer userId, String token) {
        synchronized (websocketLive){
            if (!websocketLive.containsKey(userId)) {
                websocketLive.put(userId, token);
                return true;
            }
        }
        System.out.println("已有连接+"+userId);
        return false;
    }

    /**
     * 删除表中的一条数据
     */
    public synchronized static boolean delete(Integer userId) {
        synchronized (websocketLive){
            websocketLive.remove(userId);
        }
        return true;
    }
}
