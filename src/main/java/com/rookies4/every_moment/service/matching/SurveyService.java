package com.rookies4.every_moment.service.matching;

import com.rookies4.every_moment.entity.UserEntity;
import com.rookies4.every_moment.entity.matching.SurveyResult;
import com.rookies4.every_moment.repository.matching.SurveyResultRepository;
import com.rookies4.every_moment.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SurveyService {

    private final SurveyResultRepository surveyResultRepository;
    private final UserRepository userRepository;
    private final MatchScorerService matchScorerService; // MatchScorerService를 주입받음

    // 설문 결과 저장
    public SurveyResult submitSurveyResult(Long userId, SurveyResult surveyResult) {
        Optional<UserEntity> user = userRepository.findById(userId);
        // user가 없으면 예외를 던지고 있으면 정상 처리
        UserEntity foundUser = user.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        surveyResult.setUser(foundUser);  // 사용자와 설문 결과 연결
        return surveyResultRepository.save(surveyResult);  // 설문 결과 DB에 저장 후 저장된 결과를 반환
    }

    // 설문 결과 조회 (매칭 점수 계산에 사용)
    public SurveyResult getSurveyResult(Long userId) {
        return surveyResultRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("설문 결과를 찾을 수 없습니다."));
    }

    // 설문 결과 분석 (MatchScorerService의 calculateScore를 호출)
    public double analyzeSurveyResults(SurveyResult userSurveyResult, SurveyResult matchUserSurveyResult) {
        // MatchScorerService를 사용하여 점수 계산
        return matchScorerService.calculateScore(userSurveyResult, matchUserSurveyResult); // calculateScore 메서드 호출
    }

    // 주어진 사용자 ID 목록에 대한 설문 결과를 한 번에 조회하는 메서드
    public List<SurveyResult> getSurveyResultsByUserIds(List<Long> userIds) {
        return surveyResultRepository.findAllByUserIdIn(userIds);
    }
}