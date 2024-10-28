package com.common.controller.webSocket;

import com.common.interceptor.WebSocketInterceptor;
import com.common.service.Socket.group.GroupSocketService;
import com.common.util.JwtUtil;
import com.common.util.websocket.SocketInf;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
public class ChatWSController extends TextWebSocketHandler {
    /**
     * 群聊管理器
     */
    private GroupSocketService groupSocketService;

    public ChatWSController(GroupSocketService groupSocketService) {
        this.groupSocketService = groupSocketService;
    }

    public static Integer webSocketConnectNum = 0;


    /**
     * 当 WebSocket 连接建立时调用此方法。
     *
     * @param session WebSocket 会话
     * @throws Exception 异常
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);

        Integer userID = getUserIdBySession(session);

        /**
         * 加入统一管理器*/
        groupSocketService.add(userID, session);

        synchronized (webSocketConnectNum) {
            webSocketConnectNum++;
        }

    //  System.out.println(userID + "-----连接成功" + "，当前连接数量" + (webSocketConnectNum));
    }

    /**
     * 获取session的userId
     */
    public Integer getUserIdBySession(WebSocketSession session) {
        String token = (String) session.getAttributes().get("Authorization");
        Map<String, Object> parseUserLoginToken = JwtUtil.parseUserLoginToken(token);
        Integer userID = (Integer) parseUserLoginToken.get("userId");
        return userID;
    }

    /**
     * 当 WebSocket 连接断开时调用此方法。
     *
     * @param session WebSocket 会话
     * @param status  关闭状态
     *                * @throws Exception 异常
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {


        super.afterConnectionClosed(session, status);

        Integer userID = getUserIdBySession(session);

        /**
         * 删除连接信息*/
        SocketInf.delete(userID);


        /**
         * 更新连接数量*/
        synchronized (webSocketConnectNum) {
            webSocketConnectNum = webSocketConnectNum - 1;
        }

        /**
         * 从管理器中拿走
         * */
        groupSocketService.delete(userID, session);

//        System.out.println(userID + "连接断开" + "，当前连接数量" + (webSocketConnectNum));
    }


    /**
     * 处理文本消息。
     *
     * @param session WebSocket 会话
     * @param message 文本消息
     * @throws Exception 异常
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        /**
         * 限制流量速率
         * */
        super.handleTextMessage(session, message);

        Integer userID = getUserIdBySession(session);

        /**
         * 将消息交给WebSocketChatManger
         * websocket集中处理管理器进行处理
         * */
        groupSocketService.receiveMessage(session,userID, message.getPayload(), System.currentTimeMillis());
    }
}
