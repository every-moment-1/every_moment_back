package com.rookies4.every_moment.chat.config;

import com.rookies4.every_moment.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StompHandshakeAuthInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean beforeHandshake(ServerHttpRequest req, ServerHttpResponse res,
                                   WebSocketHandler wsHandler, Map<String, Object> attrs) {
        // ✅ SockJS의 정보 요청(/info)은 인증 절차를 건너뛰도록 허용합니다.
        // 이 요청은 실제 웹소켓 연결 전에 서버의 기능을 확인하는 용도입니다.
//        if (req.getURI().getPath().contains("/info")) {
//            return true;
//        }

        String token = null;
        String roomId = null;
        if (req instanceof ServletServerHttpRequest r) {
            var http = r.getServletRequest();
            token = Optional.ofNullable(http.getHeader("Authorization"))
                    .filter(h -> h.startsWith("Bearer "))
                    .map(h -> h.substring(7))
                    .orElse(http.getParameter("token")); // /ws?token=...
            roomId = http.getParameter("roomId");
        }

        // 토큰이 없거나 유효하지 않으면 연결을 거부합니다.
        if (token == null || !jwtTokenProvider.validate(token)) {
            return false;
        }

        Claims claims = jwtTokenProvider.parse(token);
        Number uidNum = claims.get("uid", Number.class);
        if (uidNum == null) {
            return false;
        }

        attrs.put("userId", String.valueOf(uidNum.longValue()));
        attrs.put("roomId", Long.valueOf(roomId));
        return true;
    }

    @Override public void afterHandshake(ServerHttpRequest a, ServerHttpResponse b, WebSocketHandler c, Exception d) { }
}