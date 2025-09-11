package com.rookies4.every_moment.match.service;

import com.rookies4.every_moment.match.entity.Match;
import com.rookies4.every_moment.match.entity.MatchScores;
import com.rookies4.every_moment.match.entity.dto.MatchScoreDTO;
import com.rookies4.every_moment.match.repository.MatchScoresRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchScoresService {

    private final MatchScoresRepository matchScoresRepository; // MatchScores 레포지토리

    // 점수 저장
    @Transactional
    public void saveMatchScores(Match match, int user1Score, int user2Score, double similarityScore) {
        MatchScores matchScores = new MatchScores();
        matchScores.setMatch(match);  // 매칭 연관 설정
        matchScores.setUser1_Score(user1Score);
        matchScores.setUser2_Score(user2Score);
        matchScores.setSimilarityScore(similarityScore);
        matchScoresRepository.save(matchScores);  // 점수 저장
    }

    // 점수 조회
    public MatchScoreDTO getMatchScores(Long matchId) {
        MatchScores matchScores = matchScoresRepository.findByMatchId(matchId)
                .orElseThrow(() -> new IllegalArgumentException("매칭 점수를 찾을 수 없습니다."));
        return new MatchScoreDTO(
                matchScores.getMatch().getId(),
                matchScores.getUser1_Score(),
                matchScores.getUser2_Score(),
                matchScores.getSimilarityScore(),
                matchScores.getCreatedAt()
        );
    }
}