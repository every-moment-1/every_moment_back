package com.rookies4.every_moment.entity.dto.matchingDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MatchProposalDTO {
    private Long proposerId;    // 제안한 사용자 ID
    private Long targetUserId;  // 매칭 제안받은 사용자 ID
    private String proposalMessage; // 제안 메시지 (선택적)
}