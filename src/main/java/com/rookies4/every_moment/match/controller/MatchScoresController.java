package com.rookies4.every_moment.match.controller;

import com.rookies4.every_moment.match.entity.Match;
import com.rookies4.every_moment.match.entity.dto.MatchScoreDTO;
import com.rookies4.every_moment.match.repository.MatchRepository;
import com.rookies4.every_moment.match.service.MatchScoresService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/matches")
public class MatchScoresController {

    private final MatchScoresService matchScoresService;  // MatchScoresService
    private final MatchRepository matchRepository; // MatchRepository 추가
    // 점수 조회
    @GetMapping("/{matchId}/scores")
    public ResponseEntity<MatchScoreDTO> getMatchScores(@PathVariable Long matchId) {
        MatchScoreDTO matchScoreDTO = matchScoresService.getMatchScores(matchId);
        return ResponseEntity.ok(matchScoreDTO);
    }

    // 점수 저장
    @PostMapping("/{matchId}/scores")
    public ResponseEntity<String> saveMatchScores(@PathVariable Long matchId,
                                                  @RequestParam int user1Score,
                                                  @RequestParam int user2Score,
                                                  @RequestParam double similarityScore) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("매칭을 찾을 수 없습니다."));
        matchScoresService.saveMatchScores(match, user1Score, user2Score, similarityScore);
        return ResponseEntity.ok("점수가 성공적으로 저장되었습니다.");
    }
}