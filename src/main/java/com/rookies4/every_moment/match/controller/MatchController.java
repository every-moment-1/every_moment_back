package com.rookies4.every_moment.match.controller;

import com.rookies4.every_moment.match.controller.dto.MatchResponseDTO;
import com.rookies4.every_moment.entity.UserEntity;
import com.rookies4.every_moment.match.entity.dto.MatchProposalDTO;
import com.rookies4.every_moment.match.entity.Match;
import com.rookies4.every_moment.match.repository.MatchRepository;
import com.rookies4.every_moment.match.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/match")
public class MatchController {

    private final MatchService matchService;
    private final MatchRepository matchRepository;  // MatchRepository 주입

    @PostMapping("/propose")
    public ResponseEntity<MatchResponseDTO> proposeMatch(@RequestBody MatchProposalDTO proposal) {
        // 매칭 제안 후 매칭 ID 반환
        Long matchId = matchService.proposeMatch(proposal.getProposerId(), proposal);

        // 매칭 제안 성공 메시지 응답
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("매칭을 찾을 수 없습니다."));

        // 매칭의 점수 값들
        MatchResponseDTO response = new MatchResponseDTO(
                matchId,
                proposal.getProposerId(),
                proposal.getTargetUserId(),
                match.getUser1_Score(),
                match.getUser2_Score(),
                match.getSimilarityScore(),
                match.getStatus().name(),
                "매칭이 제안되었습니다."
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 매칭 수락
    @PostMapping("/accept/{matchId}")
    public ResponseEntity<MatchResponseDTO> acceptMatch(@PathVariable Long matchId) {
        matchService.acceptMatch(matchId);

        // 매칭 수락 성공 메시지 응답
        Match match = matchRepository.findById(matchId).orElseThrow(() -> new IllegalArgumentException("매칭을 찾을 수 없습니다."));
        MatchResponseDTO response = new MatchResponseDTO(
                matchId,
                null,
                null,
                match.getUser1_Score(),
                match.getUser2_Score(),
                match.getSimilarityScore(),
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
        Match match = matchRepository.findById(matchId).orElseThrow(() -> new IllegalArgumentException("매칭을 찾을 수 없습니다."));
        MatchResponseDTO response = new MatchResponseDTO(
                matchId,
                null,
                null,
                match.getUser1_Score(),
                match.getUser2_Score(),
                match.getSimilarityScore(),
                "REJECTED",
                "매칭이 거절되었습니다."
        );
        return ResponseEntity.ok(response);
    }

    // 거절된 매칭에 대해 user1과 user2 ID를 가져옴
    @PostMapping("/get-rejected-match-users/{matchId}")
    public ResponseEntity<List<Long>> getRejectedMatchUsers(
            @PathVariable Long matchId) {

        // 1. 서비스 메서드를 호출하여 거절된 매칭의 user1과 user2 ID를 가져옴
        List<Long> rejectedUserIds = matchService.getRejectedMatchUserIds(matchId);

        // 2. 응답 DTO를 구성하여 반환하는 대신, 바로 ID 목록을 반환
        return ResponseEntity.ok(rejectedUserIds);
    }

    // 스왑 신청
    @PostMapping("/swap/{matchId}")
    public ResponseEntity<MatchResponseDTO> swapMatch(@PathVariable Long matchId) {
        matchService.swapMatch(matchId);

        // 스왑 신청 성공 메시지 응답
        Match match = matchRepository.findById(matchId).orElseThrow(() -> new IllegalArgumentException("매칭을 찾을 수 없습니다."));
        MatchResponseDTO response = new MatchResponseDTO(
                matchId,
                null,
                null,
                match.getUser1_Score(),
                match.getUser2_Score(),
                match.getSimilarityScore(),
                "SWAP_REQUESTED",
                "스왑 신청이 처리되었습니다."
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //관리자가 새로운 매칭
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/propose-new/{proposerId}/{targetUserId}")
    public ResponseEntity<MatchResponseDTO> proposeNewMatch(@PathVariable Long proposerId, @PathVariable Long targetUserId) {
        // 새로운 매칭을 제안 후, 그 매칭 ID 반환
        Long matchId = matchService.proposeNewMatch(proposerId, targetUserId);

        // 매칭 제안 성공 메시지 응답
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("매칭을 찾을 수 없습니다."));

        MatchResponseDTO response = new MatchResponseDTO(
                match.getId(),
                proposerId,
                targetUserId,
                match.getUser1_Score(),
                match.getUser2_Score(),
                match.getSimilarityScore(),
                "PENDING",
                "새로운 매칭을 제안했습니다."
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}