package com.rookies4.every_moment.match.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MatchResponseDTO {
    private Long matchId;
    private Long proposerId;
    private Long targetUserId;
    private Integer user1Score;  // user1 점수
    private Integer user2Score;  // user2 점수
    private Double similarityScore;  // 유사도 점수 추가
    private String status;
    private String message;  // 매칭 상태에 대한 메시지
}