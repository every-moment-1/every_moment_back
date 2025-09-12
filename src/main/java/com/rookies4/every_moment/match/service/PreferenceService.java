package com.rookies4.every_moment.match.service;

import com.rookies4.every_moment.match.controller.dto.PreferenceResponseDTO;
import com.rookies4.every_moment.match.entity.Preference;
import com.rookies4.every_moment.match.entity.SurveyResult;
import com.rookies4.every_moment.match.repository.PreferenceRepository;
import com.rookies4.every_moment.match.repository.SurveyResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PreferenceService {

    private final PreferenceRepository preferencesRepository; // PreferencesRepository
    private final SurveyResultRepository surveyResultRepository; // SurveyResultRepository
    private final MatchScorerService matchScorerService; // 점수 계산 서비스

    // 설문 결과를 바탕으로 선호도 계산하여 Preferences 테이블에 저장
    public PreferenceResponseDTO calculateAndSavePreferences(Long userId) {
        // 사용자의 설문 결과 가져오기
        SurveyResult surveyResult = surveyResultRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("설문 결과를 찾을 수 없습니다."));

        // 가중치를 적용하여 각 항목 계산 (MatchScorerService 사용)
        double sleepTimeScore = matchScorerService.calculateSleepTimeScore(surveyResult.getSleepTime(), surveyResult.getSleepTime());
        double cleanlinessScore = matchScorerService.calculateCleanlinessScore(surveyResult.getCleanliness(), surveyResult.getCleanliness());
        double noiseSensitivityScore = matchScorerService.calculateNoiseSensitivityScore(surveyResult.getNoiseSensitivity(), surveyResult.getNoiseSensitivity());
        double heightScore = matchScorerService.calculateHeightScore(surveyResult.getHeight(), surveyResult.getHeight());
        double roomTempScore = matchScorerService.calculateRoomTempScore(surveyResult.getRoomTemp(), surveyResult.getRoomTemp());

        // Preference 객체 생성
        Preference preference = new Preference();
        preference.setUser(surveyResult.getUser());  // 설문 결과에서 사용자 가져오기
        preference.setSleepTime((int) sleepTimeScore);
        preference.setCleanliness((int) cleanlinessScore);
        preference.setNoiseSensitivity((int) noiseSensitivityScore);
        preference.setHeight((int) heightScore);
        preference.setRoomTemp((int) roomTempScore);

        // Preference 테이블에 저장
        preferencesRepository.save(preference);

        // PreferenceResponseDTO로 변환하여 반환
        return new PreferenceResponseDTO(
                preference.getId(),
                preference.getUser().getId(),
                preference.getCleanliness(),
                preference.getHeight(),
                preference.getNoiseSensitivity(),
                preference.getRoomTemp(),
                preference.getSleepTime()
        );
    }

    // 선호도 조회
    public PreferenceResponseDTO getPreferences(Long userId) {
        Preference preference = preferencesRepository.findByUserId(userId);

        if (preference != null) {
            // PreferenceResponseDTO로 변환하여 반환
            return new PreferenceResponseDTO(
                    preference.getId(),
                    preference.getUser().getId(),  // userId
                    preference.getCleanliness(),
                    preference.getHeight(),
                    preference.getNoiseSensitivity(),
                    preference.getRoomTemp(),
                    preference.getSleepTime()
            );
        } else {
            throw new IllegalArgumentException("선호도를 찾을 수 없습니다.");
        }
    }
}