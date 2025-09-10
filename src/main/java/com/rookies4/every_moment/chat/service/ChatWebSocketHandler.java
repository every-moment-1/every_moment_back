package com.rookies4.every_moment.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rookies4.every_moment.chat.domain.ChatRoom;
import com.rookies4.every_moment.chat.dto.ChatMessagePayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final ChatService chatService;
    // 채팅방별 세션을 관리하기 위한 맵
    private static final Map<Long, Map<String, WebSocketSession>> sessionsByRoom = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 핸드셰이크 인터셉터에서 저장한 roomId와 userId를 가져옵니다.
        Long roomId = (Long) session.getAttributes().get("roomId");
        String userId = (String) session.getAttributes().get("userId");

        if (roomId != null && userId != null) {
            // 채팅방별 세션 맵에 현재 세션을 추가
            sessionsByRoom.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>()).put(session.getId(), session);
            System.out.println("WebSocket 연결됨! Room ID: " + roomId + ", User ID: " + userId);
        } else {
            session.close(CloseStatus.NOT_ACCEPTABLE);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Long roomId = (Long) session.getAttributes().get("roomId");
        Long senderId = Long.valueOf((String) session.getAttributes().get("userId"));
        String payload = message.getPayload();

        // 클라이언트로부터 받은 JSON 메시지 파싱
        ChatMessagePayload chatMessage = objectMapper.readValue(payload, ChatMessagePayload.class);

        // 메시지 저장 및 처리
        ChatRoom room = chatService.getRoomIfMember(roomId, senderId);
        var saved = chatService.saveMessage(room, senderId, chatMessage.content());

        // 메시지를 JSON 형태로 변환하여 모든 방 참여자에게 브로드캐스트
        String broadcastMessage = objectMapper.writeValueAsString(saved);

        sessionsByRoom.getOrDefault(roomId, new ConcurrentHashMap<>()).values().forEach(s -> {
            try {
                s.sendMessage(new TextMessage(broadcastMessage));
            } catch (Exception e) {
                System.err.println("메시지 전송 중 오류 발생: " + e.getMessage());
            }
        });
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long roomId = (Long) session.getAttributes().get("roomId");
        if (roomId != null) {
            sessionsByRoom.get(roomId).remove(session.getId());
        }
    }
}