package com.rookies4.every_moment.controller.matching;

import com.rookies4.every_moment.entity.dto.matchingDTO.MatchDTO;
import com.rookies4.every_moment.entity.UserEntity;
import com.rookies4.every_moment.service.matching.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/match/recommendation")
public class MatchRecommendationController {

    private final MatchService matchService;

    // 여러 명의 룸메이트 추천 목록 조회
    @GetMapping("/list")
    public ResponseEntity<List<MatchDTO>> getMatchingRecommendationsList(@AuthenticationPrincipal UserEntity user) {
        List<MatchDTO> recommendations = matchService.getMatchingRecommendations(user);
        return ResponseEntity.ok(recommendations);
    }

    // 1:1 룸메이트 추천 조회
    @GetMapping("/single")
    public ResponseEntity<MatchDTO> getMatchingRecommendation(@AuthenticationPrincipal UserEntity user) {
        MatchDTO recommendation = matchService.getMatchingRecommendation(user);

        if (recommendation != null) {
            return ResponseEntity.ok(recommendation);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 추천된 룸메이트가 없을 경우
        }
    }
}