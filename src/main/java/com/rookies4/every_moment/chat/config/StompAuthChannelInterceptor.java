package com.rookies4.every_moment.chat.config;

import com.rookies4.every_moment.chat.UserPrincipal;
import com.rookies4.every_moment.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {
    private final ChatService chatService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor acc = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(acc.getCommand())) {
            Map<String, Object> attrs = acc.getSessionAttributes();
            String userId = (String) attrs.get("userId");   // ⬅ 핸드셰이크와 동일한 키
            if (userId == null) throw new IllegalArgumentException("Unauthorized");
            acc.setUser(new UserPrincipal(userId));         // ⬅ Principal 주입
        }

        if (StompCommand.SUBSCRIBE.equals(acc.getCommand())) {
            String dest = acc.getDestination(); // e.g. /topic/rooms/{roomId}
            if (acc.getUser() == null) throw new IllegalStateException("No principal on SUBSCRIBE");
            Long uid = Long.valueOf(acc.getUser().getName());
            Long roomId = extractRoomId(dest);
            chatService.getRoomIfMember(roomId, uid); // 멤버 확인
        }

        if (StompCommand.SEND.equals(acc.getCommand())) {
            String dest = acc.getDestination(); // e.g. /app/rooms/{roomId}
            if (acc.getUser() == null) throw new IllegalStateException("No principal on SEND");
            Long uid = Long.valueOf(acc.getUser().getName());
            Long roomId = extractRoomId(dest);
            chatService.getRoomIfMember(roomId, uid); // 멤버 확인
        }
        return message;
    }

    private Long extractRoomId(String dest){
        if (dest == null) throw new IllegalArgumentException("No destination");
        String[] parts = dest.split("/");
        return Long.parseLong(parts[parts.length - 1]);
    }
}