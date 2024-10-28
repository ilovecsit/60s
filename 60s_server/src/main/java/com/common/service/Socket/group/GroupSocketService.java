package com.common.service.Socket.group;

import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;

public interface GroupSocketService {

    void add(Integer userID, WebSocketSession session);

    void delete(Integer userID,WebSocketSession session);

    void receiveMessage(WebSocketSession session, Integer senderID, String message, Long sendTime);

    /**
     * 用户退出群聊后 删除 维护的 群聊 与 用户的  关系
     */
    void groupUserExit();

    void start();
}
