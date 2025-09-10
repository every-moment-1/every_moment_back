package com.rookies4.every_moment.controller.matching;

import com.rookies4.every_moment.entity.UserEntity;
import com.rookies4.every_moment.entity.dto.matchingDTO.MatchProposalDTO;
import com.rookies4.every_moment.entity.dto.matchingDTO.MatchResultDTO;
import com.rookies4.every_moment.entity.matching.MatchStatus;
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


    // 매칭 제안 처리
    @PostMapping("/propose")
    public ResponseEntity<Void> proposeMatch(@RequestBody MatchProposalDTO proposal) {
        matchService.proposeMatch(proposal.getProposerId(), proposal);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 매칭 수락
    @PostMapping("/accept/{matchId}")
    public ResponseEntity<Void> acceptMatch(@PathVariable Long matchId) {
        matchService.acceptMatch(matchId);
        return ResponseEntity.ok().build();
    }

    // 매칭 거절
    @PostMapping("/reject/{matchId}")
    public ResponseEntity<Void> rejectMatch(@PathVariable Long matchId) {
        matchService.rejectMatch(matchId);
        return ResponseEntity.ok().build();
    }

    // 기존 매칭이 거절된 경우 새로운 매칭 신청
    @PostMapping("/request-new-match/{matchId}")
    public ResponseEntity<Void> requestNewMatch(
            @PathVariable Long matchId,
            @AuthenticationPrincipal UserEntity user) {

        // 새로운 매칭 신청 로직 호출
        matchService.requestNewMatch(user.getId(), matchId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 스왑 신청 처리 (매칭 상태가 REJECTED일 때만 스왑 신청 가능)
    @PostMapping("/swap/{matchId}")
    public ResponseEntity<Void> swapMatch(@PathVariable Long matchId) {
        matchService.swapMatch(matchId);  // 스왑 신청 로직 호출
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    // 관리자가 새로운 매칭을 제안 (스왑 신청 후에만 가능)
    @PostMapping("/propose-new/{proposerId}/{targetUserId}")
    public ResponseEntity<Void> proposeNewMatch(@PathVariable Long proposerId, @PathVariable Long targetUserId) {
        matchService.proposeNewMatch(proposerId, targetUserId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}