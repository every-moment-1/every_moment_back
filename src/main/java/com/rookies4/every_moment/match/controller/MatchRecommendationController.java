package com.rookies4.every_moment.match.controller;

import com.rookies4.every_moment.match.entity.dto.MatchRecommendationDTO;
import com.rookies4.every_moment.entity.UserEntity;
import com.rookies4.every_moment.match.service.MatchRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/match/recommendation")
public class MatchRecommendationController {

    private final MatchRecommendationService matchRecommendationService;

    // 추천된 사용자 목록을 가져오기 위한 매칭 함수 (DB에 저장된 추천 결과 조회)
    @GetMapping("/list")
    public ResponseEntity<List<MatchRecommendationDTO>> getMatchingRecommendationsList(@AuthenticationPrincipal UserEntity user) {
        // 매칭 추천 결과 목록 조회
        List<MatchRecommendationDTO> recommendations = matchRecommendationService.getMatchingRecommendations(user);

        // 추천 결과가 없으면 NO_CONTENT 응답
        if (recommendations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        // 추천 결과가 있으면 200 OK 응답
        return ResponseEntity.ok(recommendations);
    }

    // 1:1 룸메이트 추천 저장
    @PostMapping("/save-recommendation")
    public ResponseEntity<Void> saveMatchRecommendation(@RequestBody MatchRecommendationDTO recommendationDTO) {
        matchRecommendationService.saveMatchRecommendation(recommendationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 1:1 룸메이트 추천 조회
    @GetMapping("/single")
    public ResponseEntity<MatchRecommendationDTO> getMatchingRecommendation(@AuthenticationPrincipal UserEntity user) {
        Optional<MatchRecommendationDTO> recommendation = matchRecommendationService.getMatchingRecommendation(user);

        return recommendation
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }
}