package com.rookies4.every_moment.match.service;

import com.rookies4.every_moment.match.entity.Match;
import com.rookies4.every_moment.match.entity.dto.MatchResultDTO;
import com.rookies4.every_moment.match.entity.MatchResult;
import com.rookies4.every_moment.match.entity.MatchStatus;
import com.rookies4.every_moment.match.entity.SurveyResult;
import com.rookies4.every_moment.match.repository.MatchRepository;
import com.rookies4.every_moment.match.repository.MatchResultRepository;
import com.rookies4.every_moment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MatchResultService {

    private final MatchResultRepository matchResultRepository;
    private final UserRepository userRepository;
    private final SurveyService surveyService;
    private final MatchScorerService matchScorerService;
    private final MatchRepository matchRepository;

    // 나의 매칭 상태 확인 (matchId, status만 반환)
    public List<MatchResultDTO> getSelfMatchResult(Long userId) {
        // 사용자와 관련된 모든 매칭을 가져옴
        List<MatchResult> matchResults = matchResultRepository.findByUserId(userId);

        // 매칭 결과가 존재하는 경우
        if (!matchResults.isEmpty()) {
            List<MatchResultDTO> matchResultDTOList = new ArrayList<>();

            // 매칭 상태별로 처리
            for (MatchResult matchResult : matchResults) {
                String status = matchResult.getStatus().name();  // 매칭 상태

                // 상태에 따라 DTO 생성
                matchResultDTOList.add(new MatchResultDTO(
                        matchResult.getId(),
                        null,  // 룸 배정은 null로 처리
                        null,  // 룸메이트 이름은 null로 처리
                        null,  // 선호도 점수는 null로 처리
                        null,  // 매칭 이유는 null로 처리
                        matchResult.getMatch() != null ? String.valueOf(matchResult.getMatch().getId()) : "UNKNOWN",  // 매칭 ID 가져오기
                        status  // 상태 추가
                ));
            }
            return matchResultDTOList;
        } else {
            throw new IllegalArgumentException("매칭을 찾을 수 없습니다.");
        }
    }

    // 자신과 상대방 매칭 상태 확인 (여러 결과 반환)
    @Transactional
    public List<MatchResultDTO> getMatchStatusResult(Long userId, Long matchUserId) {
        // 두 사용자가 매칭된 Match 데이터를 가져옴
        List<Match> matches = matchRepository.findByUser1IdAndUser2Id(userId, matchUserId);

        // 결과가 존재하는 경우
        if (!matches.isEmpty()) {
            List<MatchResultDTO> matchResultDTOList = new ArrayList<>();

            // 매칭 상태에 따라 처리
            for (Match match : matches) {
                // 상태와 매칭 ID만 반환 (PENDING, REJECTED, SWAP_REQUESTED, ACCEPTED 등)
                matchResultDTOList.add(new MatchResultDTO(
                        match.getId(),  // 룸 배정은 null로 처리
                        null,
                        null,  // 룸메이트 이름도 null로 처리
                        null,  // 선호도 점수는 null로 처리
                        null,
                        match.getId() != null ? String.valueOf(match.getId()) : "UNKNOWN",  // 매칭 ID 가져오기
                        match.getStatus() != null ? match.getStatus().name() : "UNKNOWN"  // Match 엔티티의 상태 가져오기

                ));
            }
            return matchResultDTOList;
        } else {
            throw new IllegalArgumentException("매칭을 찾을 수 없습니다.");
        }
    }

    // 매칭 결과 DTO 생성 후 반환
    public MatchResultDTO getMatchResult(Long userId, Long matchUserId) {
        // 사용자의 설문 결과 조회
        SurveyResult userSurveyResult = surveyService.getSurveyResult(userId);
        SurveyResult matchUserSurveyResult = surveyService.getSurveyResult(matchUserId);

        // 매칭 점수 계산
        double score = matchScorerService.calculateScore(userSurveyResult, matchUserSurveyResult);

        // 매칭 이유 Top 3 계산
        List<String> matchReasons = generateMatchReasons(userSurveyResult, matchUserSurveyResult);

        // 룸 배정 정보 (예: "A동 304호 (DOUBLE)" 등)
        String roomAssignment = "A동 304호 (DOUBLE)"; // 예시로 고정 배정 (나중에 로직으로 처리 가능)

        // 룸메이트 이름 (익명으로 표시)
        String roommateName = "익명";

        // 매칭 결과 DB에 저장
        MatchResult matchResult = saveMatchResult(userId, matchUserId, score, roomAssignment, roommateName, matchReasons);

        // 상태 가져오기
        String status = matchResult.getMatch().getStatus().name(); // Match 상태 가져오기

        // 매칭 ID 가져오기
        String matchId = String.valueOf(matchResult.getMatch().getId()); // 매칭 ID를 가져옴

        // 매칭 결과 DTO 생성 후 반환 (matchId와 status 포함)
        return new MatchResultDTO(
                matchResult.getId(),
                roomAssignment,
                roommateName,
                score,
                matchReasons,
                matchId,  // matchId를 직접 가져옴
                status   // 상태를 직접 가져옴
        );
    }

    private MatchResult saveMatchResult(Long userId, Long matchUserId, double score, String roomAssignment, String roommateName, List<String> matchReasons) {
        // 사용자와 매칭된 Match 객체를 찾습니다.
        List<Match> matches = matchRepository.findByUser1IdAndUser2Id(userId, matchUserId);
        if (matches.isEmpty()) {
            throw new IllegalArgumentException("매칭을 찾을 수 없습니다.");
        }

        // matches에서 첫 번째 결과를 사용
        Match match = matches.get(0);  // 첫 번째 매칭 결과 가져오기

        // 새로운 MatchResult 객체 생성
        MatchResult matchResult = new MatchResult();
        matchResult.setMatch(match);  // Match 객체 설정
        matchResult.setUser(userRepository.findById(userId).orElse(null));
        matchResult.setMatchUser(userRepository.findById(matchUserId).orElse(null));  // matchUser 설정 추가
        matchResult.setScore((int) score);
        matchResult.setRoomAssignment(roomAssignment);
        matchResult.setRoommateName(roommateName);
        matchResult.setMatchReasons(matchReasons);

        // Match 상태를 기반으로 MatchResult 상태 설정
        if (match.getStatus() == MatchStatus.ACCEPTED) {
            matchResult.setStatus(MatchStatus.ACCEPTED);
        } else if (match.getStatus() == MatchStatus.REJECTED) {
            matchResult.setStatus(MatchStatus.REJECTED);
        } else {
            matchResult.setStatus(MatchStatus.PENDING);  // 기본 상태는 PENDING
        }

        // MatchResult 객체를 DB에 저장
        return matchResultRepository.save(matchResult);
    }

    // 매칭 이유 생성
    public List<String> generateMatchReasons(SurveyResult userSurveyResult, SurveyResult matchUserSurveyResult) {
        List<String> reasons = new ArrayList<>();
        double sleepTimeSimilarity = 1 - Math.abs(userSurveyResult.getSleepTime() - matchUserSurveyResult.getSleepTime()) / 3.0;
        reasons.add("취침/기상 유사: " + String.format("%.2f", sleepTimeSimilarity));

        double cleanlinessSimilarity = 1 - Math.abs(userSurveyResult.getCleanliness() - matchUserSurveyResult.getCleanliness()) / 4.0;
        reasons.add("청결도 유사: " + String.format("%.2f", cleanlinessSimilarity));

        double noiseSensitivitySimilarity = 1 - Math.abs(userSurveyResult.getNoiseSensitivity() - matchUserSurveyResult.getNoiseSensitivity()) / 3.0;
        reasons.add("소음 민감도 차이: " + String.format("%.2f", noiseSensitivitySimilarity));

        double heightSimilarity = 1 - Math.abs(userSurveyResult.getHeight() - matchUserSurveyResult.getHeight()) / 3.0;
        reasons.add("키 유사: " + String.format("%.2f", heightSimilarity));

        double roomTempSimilarity = 1 - Math.abs(userSurveyResult.getRoomTemp() - matchUserSurveyResult.getRoomTemp()) / 3.0;
        reasons.add("방 온도 유사: " + String.format("%.2f", roomTempSimilarity));

        reasons.sort((r1, r2) -> Double.compare(
                Double.parseDouble(r2.split(": ")[1]),
                Double.parseDouble(r1.split(": ")[1])
        ));

        return reasons.subList(0, 3); // 상위 3개 항목
    }
}