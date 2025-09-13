package com.rookies4.every_moment.match.controller;

import com.rookies4.every_moment.match.entity.dto.MatchRecommendationDTO;
import com.rookies4.every_moment.entity.UserEntity;
import com.rookies4.every_moment.match.entity.dto.MatchResultDTO;
import com.rookies4.every_moment.match.service.MatchRecommendationService;
import com.rookies4.every_moment.repository.UserRepository;
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
    private final UserRepository userRepository;

    // 추천된 사용자 목록을 가져오기 위한 매칭 함수 (userId를 PathVariable로 받음)

    @GetMapping("/list/{userId}") // userId를 URL에서 받습니다.
    public ResponseEntity<List<MatchRecommendationDTO>> getMatchingRecommendationsList(@PathVariable Long userId) {

        // 주어진 userId를 사용하여 매칭 추천 목록을 가져옵니다.
        List<MatchRecommendationDTO> recommendations = matchRecommendationService.getMatchingRecommendations(userId);

        if (recommendations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok(recommendations);
    }

}