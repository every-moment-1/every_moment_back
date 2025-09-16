package com.rookies4.every_moment.match.entity.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatchResultDTO {
    private Long id;
    private String roomAssignment;  // 배정된 룸 (예: A동 304호, DOUBLE)
    private String roommateName;    // 룸메이트 이름 (익명 처리)
    private Double preferenceScore; // 선호도 점수 (0 ~ 100 범위)
    private List<String> matchReasons; // 매칭 이유 Top 3
    private String matchId; // 매칭 ID (나중에 수락/거절/스왑 신청을 위한 사용)
    private String status;  // 매칭 상태 (PENDING, REJECTED, SWAP_REQUESTED 등)

    // ✅ 추가
    private Long userId;        // 한쪽
    private Long matchUserId;   // 상대
    private String userName;        // 선택
    private String matchUserName;   // 선택
}
