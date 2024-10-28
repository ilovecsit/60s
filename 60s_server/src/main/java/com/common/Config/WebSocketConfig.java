package com.common.Config;

import com.common.controller.webSocket.ChatWSController;
import com.common.interceptor.WebSocketInterceptor;
import com.common.service.Socket.group.GroupSocketService;
import com.common.service.Socket.group.imp.GroupSocketServiceImp;
import jakarta.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

/**
 * WebSocket 配置类，用于配置 WebSocket 相关的设置。
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    /**
     * 注入拦截器
     */
    @Autowired
    private WebSocketInterceptor webSocketInterceptor;

    /**
     * 注入配置数据*/
    @Autowired
    private DataConfig dataConfig;

    /**
     * 注入websocket群聊管理器*/
    @Autowired
    private GroupSocketService groupSocketService;

    /**
     * 注册 WebSocket 处理器。
     *
     * @param registry WebSocket 处理器注册表
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ChatWSController(groupSocketService),"/chat")
                .addInterceptors(webSocketInterceptor)
                .setAllowedOrigins("*");
    }

    /**
     * 创建 WebSocket 容器工厂。
     * @param servletContext Servlet 上下文
     * @return WebSocket 容器工厂
     */
    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer(ServletContext servletContext) {
        // 创建 WebSocket 容器工厂
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setServletContext(servletContext);

        // 设置最大文本消息缓冲区大小
        container.setMaxTextMessageBufferSize(8*1024); // 例如 8KB
        // 设置最大二进制消息缓冲区大小
        container.setMaxBinaryMessageBufferSize(8*1024); // 例如 8KB

        // 设置连接的最长空闲时长，
        container.setMaxSessionIdleTimeout(dataConfig.WEB_SOCKET_MAX_LEISURE_SECOND*1000l);

        return container;
    }
}
