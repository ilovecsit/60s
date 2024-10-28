package com.common.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Configuration
public class DataConfig {
    @Value("${myWebSocketConfig.maxConnect}")
    public int WEB_SOCKET_MAX_CONNECT;

    /**
     * WebSocket 连接 最大空闲时长 秒
     * */
    @Value("${myWebSocketConfig.maxLeisureMinute}")
    public int WEB_SOCKET_MAX_LEISURE_SECOND;
}
