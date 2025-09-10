package com.rookies4.every_moment.chat.config;

import com.rookies4.every_moment.chat.service.ChatWebSocketHandler;
import com.rookies4.every_moment.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket // STOMP 대신 순수 WebSocket을 활성화합니다.
@RequiredArgsConstructor
public class ChatWebSocketConfig implements WebSocketConfigurer {

    private final ChatWebSocketHandler chatWebSocketHandler;
    private final StompHandshakeAuthInterceptor handshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 순수 WebSocket 핸들러를 등록하고 핸드셰이크 인터셉터를 추가합니다.
        registry.addHandler(chatWebSocketHandler, "/ws")
                .setAllowedOriginPatterns("*")
                .setAllowedOrigins("*")
                .addInterceptors(handshakeInterceptor);
    }
}