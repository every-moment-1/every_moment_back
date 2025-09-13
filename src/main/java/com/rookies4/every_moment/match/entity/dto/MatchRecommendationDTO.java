package com.rookies4.every_moment.match.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MatchRecommendationDTO {

    private Long userId;        // 매칭된 사용자 ID
    private String username;    // 매칭된 사용자 이름 (익명으로 표시)
    private Integer score;       // 매칭 점수 (적합도)
    private String status;      // 매칭 상태 (PENDING, ACCEPTED, REJECTED, SWAP_REQUESTED)
    private String roommateName; // 룸메이트 이름 (익명으로 표시)
    private Double preferenceScore; // 룸메이트 선호도 (0~100 범위)
}

