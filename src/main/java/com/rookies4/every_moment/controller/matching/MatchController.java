package com.rookies4.every_moment.controller.matching;

import com.rookies4.every_moment.controller.dto.MatchResponseDTO;
import com.rookies4.every_moment.entity.UserEntity;
import com.rookies4.every_moment.entity.dto.matchingDTO.MatchProposalDTO;
import com.rookies4.every_moment.entity.matching.Match;
import com.rookies4.every_moment.repository.matching.MatchRepository;
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
    private final MatchRepository matchRepository;  // MatchRepository 주입

    // 매칭 제안
    @PostMapping("/propose")
    public ResponseEntity<MatchResponseDTO> proposeMatch(@RequestBody MatchProposalDTO proposal) {
        // 매칭 제안 후 매칭 ID 반환
        Long matchId = matchService.proposeMatch(proposal.getProposerId(), proposal);

        // 매칭 제안 성공 메시지 응답
        Match match = matchRepository.findById(matchId).orElseThrow(() -> new IllegalArgumentException("매칭을 찾을 수 없습니다."));
        MatchResponseDTO response = new MatchResponseDTO(
                matchId,  // 매칭 ID
                proposal.getProposerId(),
                proposal.getTargetUserId(),
                match.getUser1Score(),  // user1 점수
                match.getUser2Score(),  // user2 점수
                match.getSimilarityScore(),  // 유사도 점수 포함
                match.getStatus().name(),  // 상태: PENDING
                "매칭이 제안되었습니다."  // 메시지
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
                null,  // proposerId는 필요없음
                null,  // targetUserId는 필요없음
                match.getUser1Score(),  // user1 점수
                match.getUser2Score(),  // user2 점수
                match.getSimilarityScore(),  // 유사도 점수
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
                null,  // proposerId는 필요없음
                null,  // targetUserId는 필요없음
                match.getUser1Score(),  // user1 점수
                match.getUser2Score(),  // user2 점수
                match.getSimilarityScore(),  // 유사도 점수
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
        Match match = matchRepository.findById(matchId).orElseThrow(() -> new IllegalArgumentException("매칭을 찾을 수 없습니다."));
        MatchResponseDTO response = new MatchResponseDTO(
                matchId,
                null,
                null,
                match.getUser1Score(),
                match.getUser2Score(),
                match.getSimilarityScore(),
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
        Match match = matchRepository.findById(matchId).orElseThrow(() -> new IllegalArgumentException("매칭을 찾을 수 없습니다."));
        MatchResponseDTO response = new MatchResponseDTO(
                matchId,
                null,
                null,
                match.getUser1Score(),
                match.getUser2Score(),
                match.getSimilarityScore(),
                "SWAP_REQUESTED",
                "스왑 신청이 처리되었습니다."
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 관리자가 새로운 매칭
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/propose-new/{proposerId}/{targetUserId}")
    public ResponseEntity<MatchResponseDTO> proposeNewMatch(@PathVariable Long proposerId, @PathVariable Long targetUserId) {
        // 새로운 매칭을 제안 후, 그 매칭 ID 반환
        Long matchId = matchService.proposeNewMatch(proposerId, targetUserId);

        // 매칭 제안 성공 메시지 응답
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("매칭을 찾을 수 없습니다."));

        MatchResponseDTO response = new MatchResponseDTO(
                match.getId(),  // 새로운 매칭의 ID
                proposerId,
                targetUserId,
                match.getUser1Score(),  // user1 점수
                match.getUser2Score(),  // user2 점수
                match.getSimilarityScore(),  // 유사도 점수
                "PENDING",
                "새로운 매칭을 제안했습니다."
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}