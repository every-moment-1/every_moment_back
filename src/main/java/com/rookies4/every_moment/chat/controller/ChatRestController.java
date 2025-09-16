package com.rookies4.every_moment.chat.controller;

import com.rookies4.every_moment.chat.domain.ChatRoom;
import com.rookies4.every_moment.chat.dto.CreateRoomRequest;
import com.rookies4.every_moment.chat.service.ChatService;
import com.rookies4.every_moment.entity.UserEntity;
import com.rookies4.every_moment.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRestController {
    private final ChatService chatService;
    private final JwtTokenProvider jwt;

    // ---- 공통 유틸 ----
    private Claims parseClaims(HttpServletRequest request){
        String h = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = (h != null && h.startsWith("Bearer ")) ? h.substring(7) : null;
        return jwt.parse(token);
    }
    private Long currentUserId(HttpServletRequest request){
        Claims c = parseClaims(request);
        return c.get("uid", Number.class).longValue();
    }
    private static boolean containsAdmin(Object v){
        if (v == null) return false;
        if (v instanceof String s) {
            String[] parts = s.split("[,\\s]+");
            for (String p: parts) {
                if ("ADMIN".equalsIgnoreCase(p) || "ROLE_ADMIN".equalsIgnoreCase(p)) return true;
            }
            return false;
        }
        if (v instanceof Iterable<?> it) {
            for (Object o: it) if (containsAdmin(o)) return true;
            return false;
        }
        String s = String.valueOf(v);
        return "ADMIN".equalsIgnoreCase(s) || "ROLE_ADMIN".equalsIgnoreCase(s);
    }
    /** 토큰의 role/roles/authorities/scope 중 아무거나에 ADMIN/ROLE_ADMIN 있으면 true */
    private boolean isAdminFromToken(HttpServletRequest request){
        Claims c = parseClaims(request);
        return containsAdmin(c.get("role"))
                || containsAdmin(c.get("roles"))
                || containsAdmin(c.get("authorities"))
                || containsAdmin(c.get("scope"))
                || containsAdmin(c.get("scopes"));
    }

    private static boolean isAdminRole(Object role){
        String name = String.valueOf(role);
        return "ADMIN".equalsIgnoreCase(name) || "ROLE_ADMIN".equalsIgnoreCase(name);
    }

    // ---- 엔드포인트 ----

    @PostMapping("/rooms")
    public RoomRes createOrGetRoom(HttpServletRequest req, @RequestBody CreateRoomRequest body){
        Long me = currentUserId(req);
        ChatRoom room = chatService.getOrCreateRoom(me, body.opponentUserId());
        return toRes(room); // 모든 필드 채워 반환
    }

    @GetMapping("/rooms/{roomId}/messages")
    public Page<MsgRes> history(HttpServletRequest req,
                                @PathVariable Long roomId,
                                @RequestParam(defaultValue="0") int page,
                                @RequestParam(defaultValue="30") int size){
        Long me = currentUserId(req);
        boolean admin = isAdminFromToken(req) || chatService.isAdminUser(me);  // <- 토큰 OR DB
        if (!admin) chatService.getRoomIfMember(roomId, me);                   // 일반 유저만 멤버십 검사
        return chatService.getMessages(roomId, page, size)
                .map(m -> new MsgRes(
                        m.getId(),
                        m.getRoom().getId(),
                        m.getSenderId(),
                        m.getContent(),
                        m.getCreatedAt().toString()
                ));
    }

    /**
     * 관리자 전용 뷰 분리:
     * - view=mine(기본): 유저계정
     * - view=users: 유저↔유저(관리자 미포함)
     * - view=staff: 관리자 문의
     */
    @GetMapping("/rooms")
    public List<RoomRes> getRooms(HttpServletRequest req,
                                  @RequestParam(name="view", defaultValue="mine") String view) {
        Long me = currentUserId(req);
        boolean admin = isAdminFromToken(req) || chatService.isAdminUser(me);  // <- 토큰 OR DB

        // 일반 유저
        if (!admin) {
            return chatService.getUserChatRooms(me).stream()
                    .map(this::toRes)
                    .toList();
        }

        // 관리자
        switch (view.toLowerCase()) {
            case "users": // 관리자 미포함 방만
                return chatService.getAllRoomsWithUsers().stream()
                        .filter(r -> !isAdminRole(r.getUserA().getRole()) && !isAdminRole(r.getUserB().getRole()))
                        .map(this::toRes)
                        .toList();
            case "staff": // 관리자 포함된 방만(문의)
                return chatService.getAllRoomsWithUsers().stream()
                        .filter(r -> isAdminRole(r.getUserA().getRole()) || isAdminRole(r.getUserB().getRole()))
                        .map(this::toRes)
                        .toList();
            case "mine":  // 혹시라도 관리자가 mine을 요청하면 본인 방
            default:
                return chatService.getUserChatRooms(me).stream()
                        .map(this::toRes)
                        .toList();
        }
    }

    // ---- DTO 변환 & 보조 ----
    private RoomRes toRes(ChatRoom r){
        UserEntity a = r.getUserA();
        UserEntity b = r.getUserB();
        return new RoomRes(
                r.getId(),
                a.getId(), safeName(a), String.valueOf(a.getRole()),
                b.getId(), safeName(b), String.valueOf(b.getRole())
        );
    }
    private String safeName(UserEntity u){
        String name = u.getUsername();
        return (name != null && !name.isBlank()) ? name : "익명";
    }

    public record RoomRes(
            Long id,
            Long userAId, String userAName, String userARole,
            Long userBId, String userBName, String userBRole
    ) {}
    public record MsgRes(Long id, Long roomId, Long senderId, String content, String createdAt){}
}
