package com.rookies4.every_moment.match.controller;

import com.rookies4.every_moment.match.entity.dto.MatchRecommendationDTO;
import com.rookies4.every_moment.entity.UserEntity;
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

    @GetMapping("/list") // URL에서 {userId}를 제거합니다.
    public ResponseEntity<List<MatchRecommendationDTO>> getMatchingRecommendationsList(@AuthenticationPrincipal UserEntity user) {

        // 스프링 시큐리티가 사용자를 인증했는지 확인합니다.
        if (user == null) {
            // 인증되지 않은 사용자는 접근을 거부합니다.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 이제 userId는 URL에서 오는 것이 아니라, 안전하게 인증된 user 객체에서 가져옵니다.
        List<MatchRecommendationDTO> recommendations = matchRecommendationService.getMatchingRecommendations(user);

        if (recommendations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok(recommendations);
    }

}