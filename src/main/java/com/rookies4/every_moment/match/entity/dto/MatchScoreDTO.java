package com.rookies4.every_moment.match.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MatchScoreDTO {
    private Long matchId;            // 매칭 ID
    private Integer user1Score;      // user1 점수
    private Integer user2Score;      // user2 점수
    private Double similarityScore;  // 유사도 점수
    private LocalDateTime createdAt; // 생성일
}