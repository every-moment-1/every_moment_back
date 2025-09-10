package com.rookies4.every_moment.entity.dto.matchingDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MatchDTO {

    private Long userId;        // 매칭된 사용자 ID
    private String username;    // 매칭된 사용자 이름 (익명으로 표시)
    private Integer score;       // 매칭 점수 (적합도)
    private String status;      // 매칭 상태 (PENDING, ACCEPTED, REJECTED, SWAP_REQUESTED)
    private List<String> matchReasons; // 매칭 이유 리스트
    private String roommateName; // 룸메이트 이름 (익명으로 표시)
    private Double preferenceScore; // 룸메이트 선호도 (0~1 범위)
}

