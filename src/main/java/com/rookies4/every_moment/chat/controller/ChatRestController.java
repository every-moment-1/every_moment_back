package com.rookies4.every_moment.chat.controller;

import com.rookies4.every_moment.chat.domain.ChatMessage;
import com.rookies4.every_moment.chat.domain.ChatRoom;
import com.rookies4.every_moment.chat.dto.CreateRoomRequest;
import com.rookies4.every_moment.chat.service.ChatService;
import com.rookies4.every_moment.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRestController {
    private final ChatService chatService;
    private final JwtTokenProvider jwt;

    private Long currentUserId(HttpServletRequest request){
        String h = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = (h != null && h.startsWith("Bearer ")) ? h.substring(7) : null;
        Claims c = jwt.parse(token);
        return c.get("uid", Number.class).longValue();
    }

    @PostMapping("/rooms")
    public RoomRes createOrGetRoom(HttpServletRequest req, @RequestBody CreateRoomRequest body){
        Long me = currentUserId(req);
        ChatRoom room = chatService.getOrCreateRoom(me, body.opponentUserId());
        return new RoomRes(room.getId(), room.getUserAId(), room.getUserBId());
    }

    @GetMapping("/rooms/{roomId}/messages")
    public Page<MsgRes> history(HttpServletRequest req,
                                @PathVariable Long roomId,
                                @RequestParam(defaultValue="0") int page,
                                @RequestParam(defaultValue="30") int size){
        Long me = currentUserId(req);
        chatService.getRoomIfMember(roomId, me);
        return chatService.getMessages(roomId, page, size)
                .map(m -> new MsgRes(m.getId(), m.getRoom().getId(),
                        m.getSenderId(), m.getContent(),
                        m.getCreatedAt().toString()));
    }


    @GetMapping("/rooms")
    public List<RoomRes> getRooms(HttpServletRequest req) {
        Long me = currentUserId(req);
        return chatService.getUserChatRooms(me)
                .stream()
                .map(room -> new RoomRes(room.getId(), room.getUserAId(), room.getUserBId()))
                .collect(Collectors.toList());
    }
    public record RoomRes(Long id, Long userAId, Long userBId){}
    public record MsgRes(Long id, Long roomId, Long senderId, String content, String createdAt){}
}