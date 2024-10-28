package com.common.interceptor;


import com.common.util.LoginToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

import static com.common.controller.webSocket.ChatWSController.webSocketConnectNum;

@Component
public class WebSocketInterceptor implements HandshakeInterceptor {

    @Value("${myWebSocketConfig.maxConnect}")
    private int WEB_SOCKET_MAX_CONNECT;

    /**
     * 检查
     * 1.token是否合法
     * 2.是否已经建立websocket连接
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {


        String authorization = UriComponentsBuilder.fromHttpRequest(request).build().getQueryParams().getFirst("Authorization");

        attributes.put("Authorization", authorization);

        //      检查请求的token是否合法
        if (LoginToken.validateToken(authorization) && webSocketConnectNumValidate()) {
            return true;
        }

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false;
    }

    /**
     * 连接数量检查
     */
    public boolean webSocketConnectNumValidate() {
        return webSocketConnectNum <= WEB_SOCKET_MAX_CONNECT;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }

}
