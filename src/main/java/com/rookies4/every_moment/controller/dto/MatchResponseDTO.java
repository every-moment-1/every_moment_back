package com.rookies4.every_moment.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MatchResponseDTO {
    private Long matchId;
    private Long proposerId;
    private Long targetUserId;
    private String status;
    private String message;  // 매칭 상태에 대한 메시지
}