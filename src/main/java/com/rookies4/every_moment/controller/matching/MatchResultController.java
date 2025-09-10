package com.rookies4.every_moment.controller.matching;

import com.rookies4.every_moment.entity.dto.matchingDTO.MatchResultDTO;
import com.rookies4.every_moment.service.matching.MatchResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/match/result")
public class MatchResultController {

    private final MatchResultService matchResultService;

    // 나의 매칭 상태 확인 (자기 자신만의 매칭)
    @GetMapping("{userId}")
    public ResponseEntity<MatchResultDTO> getSelfMatchResult(@PathVariable Long userId) {
        MatchResultDTO result = matchResultService.getSelfMatchResult(userId); // 자기 자신의 매칭 상태를 확인
        return ResponseEntity.ok(result);
    }

    // 자신과 상대방 매칭 상태 확인
    @GetMapping("/result/{userId}/{matchUserId}")
    public ResponseEntity<MatchResultDTO> getMatchStatusResult(@PathVariable Long userId, @PathVariable Long matchUserId) {
        MatchResultDTO result = matchResultService.getMatchStatusResult(userId, matchUserId);
        return ResponseEntity.ok(result);
    }

    // 매칭 결과 조회
    @GetMapping("/{userId}/{matchUserId}")
    public ResponseEntity<MatchResultDTO> getMatchResult(@PathVariable Long userId, @PathVariable Long matchUserId) {
        MatchResultDTO result = matchResultService.getMatchResult(userId, matchUserId);
        return ResponseEntity.ok(result);
    }
}