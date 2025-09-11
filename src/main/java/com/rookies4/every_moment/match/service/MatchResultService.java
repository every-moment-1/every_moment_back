package com.rookies4.every_moment.match.service;

import com.rookies4.every_moment.match.entity.dto.MatchResultDTO;
import com.rookies4.every_moment.match.entity.MatchResult;
import com.rookies4.every_moment.match.entity.MatchStatus;
import com.rookies4.every_moment.match.entity.SurveyResult;
import com.rookies4.every_moment.match.repository.MatchResultRepository;
import com.rookies4.every_moment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MatchResultService {

    private final MatchResultRepository matchResultRepository;
    private final UserRepository userRepository;
    private final SurveyService surveyService;
    private final MatchScorerService matchScorerService;

    // 나의 매칭 상태 확인 (자기 자신과의 매칭은 제외하고, 나와 상대방의 매칭만 확인)
    public MatchResultDTO getSelfMatchResult(Long userId) {
        // 나의 매칭 상태는 내가 제안하거나 받은 매칭에 대해서만 조회한다.
        List<MatchResult> matchResults = matchResultRepository.findByUserId(userId); // 사용자와 관련된 모든 매칭 가져오기

        // 매칭 결과가 존재하는 경우
        if (!matchResults.isEmpty()) {
            MatchResult matchResult = matchResults.get(0);  // 첫 번째 매칭 결과를 반환하거나, 필요에 따라 다른 방식으로 처리

            // 매칭 상태가 PENDING, REJECTED, SWAP_REQUESTED일 경우, 상태만 반환
            if (matchResult.getStatus().equals(MatchStatus.PENDING) ||
                    matchResult.getStatus().equals(MatchStatus.REJECTED) ||
                    matchResult.getStatus().equals(MatchStatus.SWAP_REQUESTED)) {
                return new MatchResultDTO(
                        matchResult.getStatus().name(),  // 상태만 반환 (PENDING, REJECTED, SWAP_REQUESTED)
                        null,  // 룸 배정
                        null,  // 룸메이트 이름
                        null,  // 선호도 점수 (null로 반환)
                        null,  // 매칭 이유 (null로 반환)
                        String.valueOf(matchResult.getId())  // 매칭 ID를 String으로 변환하여 반환
                );
            }

            // 매칭이 ACCEPTED일 경우 매칭 결과 반환
            return new MatchResultDTO(
                    matchResult.getRoomAssignment(),
                    matchResult.getRoommateName(),
                    matchResult.getScore() != null ? (double) matchResult.getScore() : 0.0,  // 선호도 점수 (0 ~ 100 범위로 변환된 점수)
                    matchResult.getMatchReasons() != null ? matchResult.getMatchReasons() : new ArrayList<>(),  // 매칭 이유 (null이 아닌 빈 리스트로 대체)
                    String.valueOf(matchResult.getId()) ,  // 매칭 ID
                    matchResult.getStatus().name()  // 상태 추가
            );
        } else {
            throw new IllegalArgumentException("매칭을 찾을 수 없습니다.");
        }
    }

    // 나와 상대방의 매칭 상태 확인
    public MatchResultDTO getMatchStatusResult(Long userId, Long matchUserId) {
        Optional<MatchResult> matchResultOptional = matchResultRepository.findByUserIdAndMatchUserId(userId, matchUserId);

        if (matchResultOptional.isPresent()) {
            MatchResult matchResult = matchResultOptional.get();

            // 매칭 상태가 PENDING, REJECTED, SWAP_REQUESTED 상태일 때는 상태만 반환
            if (matchResult.getStatus().equals(MatchStatus.PENDING) ||
                    matchResult.getStatus().equals(MatchStatus.REJECTED) ||
                    matchResult.getStatus().equals(MatchStatus.SWAP_REQUESTED)) {

                return new MatchResultDTO(
                        matchResult.getStatus().name(),  // 상태만 반환 (PENDING, REJECTED, SWAP_REQUESTED)
                        null,  // 룸 배정
                        null,  // 룸메이트 이름
                        null,  // 선호도 점수 (null로 반환)
                        null,  // 매칭 이유 (null로 반환)
                        String.valueOf(matchResult.getId())  // 매칭 ID를 String으로 변환하여 반환
                );
            }

            // 매칭이 ACCEPTED일 경우 매칭 결과 반환
            return new MatchResultDTO(
                    matchResult.getRoomAssignment(),
                    matchResult.getRoommateName(),
                    matchResult.getScore() != null ? (double) matchResult.getScore() : 0.0,  // 선호도 점수 (0 ~ 100 범위로 변환된 점수)
                    matchResult.getMatchReasons() != null ? matchResult.getMatchReasons() : new ArrayList<>(),  // 매칭 이유 (null이 아닌 빈 리스트로 대체)
                    String.valueOf(matchResult.getId()) ,  // 매칭 ID
                    matchResult.getStatus().name()  // 상태 추가
            );
        } else {
            throw new IllegalArgumentException("매칭을 찾을 수 없습니다.");
        }
    }

    // 매칭 결과 페이지를 위한 데이터 생성
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
        MatchResult matchResult = new MatchResult();
        matchResult.setUser(userRepository.findById(userId).orElse(null));
        matchResult.setMatchUser(userRepository.findById(matchUserId).orElse(null));
        matchResult.setScore((int) score);
        matchResult.setRoomAssignment(roomAssignment);
        matchResult.setRoommateName(roommateName);
        matchResult.setMatchReasons(matchReasons);
        matchResult.setStatus(MatchStatus.PENDING); // 여기서 직접 MatchStatus 사용

        matchResultRepository.save(matchResult);  // DB에 저장

        // 매칭 결과 DTO 생성 후 반환
        return new MatchResultDTO(
                roomAssignment,
                roommateName,
                score,
                matchReasons,
                String.valueOf(matchResult.getId()),  // 저장된 매칭 ID
                matchResult.getStatus().name()
        );
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