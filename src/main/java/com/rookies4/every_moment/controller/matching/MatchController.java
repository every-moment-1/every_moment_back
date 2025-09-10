package com.rookies4.every_moment.controller.matching;

import com.rookies4.every_moment.controller.dto.MatchResponseDTO;
import com.rookies4.every_moment.entity.UserEntity;
import com.rookies4.every_moment.entity.dto.matchingDTO.MatchProposalDTO;
import com.rookies4.every_moment.service.matching.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/match")
public class MatchController {

    private final MatchService matchService;

    // 매칭 제안
    @PostMapping("/propose")
    public ResponseEntity<MatchResponseDTO> proposeMatch(@RequestBody MatchProposalDTO proposal) {
        // 매칭 제안 후 매칭 ID 반환
        Long matchId = matchService.proposeMatch(proposal.getProposerId(), proposal);

        // 매칭 제안 성공 메시지 응답
        MatchResponseDTO response = new MatchResponseDTO(
                matchId,  // 매칭 ID
                proposal.getProposerId(),  // 제안자 ID
                proposal.getTargetUserId(),  // 대상자 ID
                "PENDING",  // 상태: PENDING으로 설정 (추후 상태 업데이트 시 변경 가능)
                "매칭이 제안되었습니다."  // 제안 메시지
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 매칭 수락
    @PostMapping("/accept/{matchId}")
    public ResponseEntity<MatchResponseDTO> acceptMatch(@PathVariable Long matchId) {
        matchService.acceptMatch(matchId);

        // 매칭 수락 성공 메시지 응답
        MatchResponseDTO response = new MatchResponseDTO(
                matchId,
                null,  // proposerId는 필요없음
                null,  // targetUserId는 필요없음
                "ACCEPTED",
                "매칭이 수락되었습니다."
        );
        return ResponseEntity.ok(response);
    }

    // 매칭 거절
    @PostMapping("/reject/{matchId}")
    public ResponseEntity<MatchResponseDTO> rejectMatch(@PathVariable Long matchId) {
        matchService.rejectMatch(matchId);

        // 매칭 거절 성공 메시지 응답
        MatchResponseDTO response = new MatchResponseDTO(
                matchId,
                null,  // proposerId는 필요없음
                null,  // targetUserId는 필요없음
                "REJECTED",
                "매칭이 거절되었습니다."
        );
        return ResponseEntity.ok(response);
    }

    // 새로운 매칭 신청
    @PostMapping("/request-new-match/{matchId}")
    public ResponseEntity<MatchResponseDTO> requestNewMatch(
            @PathVariable Long matchId,
            @AuthenticationPrincipal UserEntity user) {

        matchService.requestNewMatch(user.getId(), matchId);

        // 새로운 매칭 신청 성공 메시지 응답
        MatchResponseDTO response = new MatchResponseDTO(
                matchId,
                null,
                null,
                "PENDING",
                "새로운 매칭이 신청되었습니다."
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 스왑 신청
    @PostMapping("/swap/{matchId}")
    public ResponseEntity<MatchResponseDTO> swapMatch(@PathVariable Long matchId) {
        matchService.swapMatch(matchId);

        // 스왑 신청 성공 메시지 응답
        MatchResponseDTO response = new MatchResponseDTO(
                matchId,
                null,
                null,
                "SWAP_REQUESTED",
                "스왑 신청이 처리되었습니다."
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 관리자가 새로운 매칭
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/propose-new/{proposerId}/{targetUserId}")
    public ResponseEntity<MatchResponseDTO> proposeNewMatch(@PathVariable Long proposerId, @PathVariable Long targetUserId) {
        matchService.proposeNewMatch(proposerId, targetUserId);

        // 새로운 매칭 제안 성공 메시지 응답
        MatchResponseDTO response = new MatchResponseDTO(
                null, // 여기서는 필요 없을 수 있음
                proposerId,
                targetUserId,
                "PENDING",
                "새로운 매칭을 제안했습니다."
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}