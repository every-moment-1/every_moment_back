package com.rookies4.every_moment.match.controller;

import com.rookies4.every_moment.match.entity.dto.MatchResultDTO;
import com.rookies4.every_moment.match.service.MatchResultService;
import com.rookies4.every_moment.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/match/result")
public class MatchResultController {

    private final MatchResultService matchResultService;
    private final JwtTokenProvider jwt; // ✅ chat과 동일하게 주입

    // ===== 공통 유틸(chat과 동일) =====
    private Claims parseClaims(HttpServletRequest request){
        String h = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = (h != null && h.startsWith("Bearer ")) ? h.substring(7) : null;
        return jwt.parse(token);
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
    /** 토큰의 role/roles/authorities/scope/scopes 중 아무거나에 ADMIN/ROLE_ADMIN 있으면 true */
    private boolean isAdminFromToken(HttpServletRequest request){
        Claims c = parseClaims(request);
        return containsAdmin(c.get("role"))
                || containsAdmin(c.get("roles"))
                || containsAdmin(c.get("authorities"))
                || containsAdmin(c.get("scope"))
                || containsAdmin(c.get("scopes"));
    }

    // ===== 기존 엔드포인트(유지) =====

    /** 나의 매칭 상태 확인 (자기 자신만의 매칭들) */
    @GetMapping("/{userId}")
    public ResponseEntity<List<MatchResultDTO>> getSelfMatchResult(@PathVariable Long userId) {
        List<MatchResultDTO> result = matchResultService.getSelfMatchResult(userId);
        return ResponseEntity.ok(result);
    }

    /** 자신과 상대방 매칭 상태 확인 (여러 매칭 결과) */
    @GetMapping("/status/{userId}/{matchUserId}")
    public ResponseEntity<List<MatchResultDTO>> getMatchStatusResult(@PathVariable Long userId,
                                                                     @PathVariable Long matchUserId) {
        List<MatchResultDTO> result = matchResultService.getMatchStatusResult(userId, matchUserId);
        return ResponseEntity.ok(result);
    }

    /** 매칭 결과 생성/조회(저장 포함) */
    @GetMapping("/result/{userId}/{matchUserId}")
    public ResponseEntity<MatchResultDTO> getMatchResult(@PathVariable Long userId,
                                                         @PathVariable Long matchUserId) {
        MatchResultDTO result = matchResultService.getMatchResult(userId, matchUserId);
        return ResponseEntity.ok(result);
    }

    // ===== ✅ 관리자 전용 전체 최신 조회 =====
    // 페어별 최신 1건만 전체 반환 (일반 유저는 403)
    @GetMapping("/admin/current")
    public ResponseEntity<?> getAllCurrentForAdmin(HttpServletRequest req) {
        boolean admin = isAdminFromToken(req);
        if (!admin) {
            return ResponseEntity.status(403).body("관리자만 접근 가능합니다.");
        }
        List<MatchResultDTO> data = matchResultService.getAllCurrentForAdmin();
        return ResponseEntity.ok(data);
    }
}
