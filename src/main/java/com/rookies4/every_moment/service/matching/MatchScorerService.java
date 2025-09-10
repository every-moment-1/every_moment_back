package com.rookies4.every_moment.service.matching;

import com.rookies4.every_moment.entity.matching.SurveyResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
@Getter
@AllArgsConstructor
public class MatchScorerService {

    // 가중치 상수들
    public static final int SLEEP_TIME_WEIGHT = 40;
    public static final int CLEANLINESS_WEIGHT = 18;
    public static final int NOISE_SENSITIVITY_WEIGHT = 18;
    public static final int HEIGHT_WEIGHT = 10;
    public static final int ROOM_TEMP_WEIGHT = 14;

    // 설문 결과를 비교하여 매칭 점수 계산
    public double calculateScore(SurveyResult userSurveyResult, SurveyResult matchUserSurveyResult) {
        double score = 0;

        // 수면 시간
        score += calculateSleepTimeScore(userSurveyResult.getSleepTime(), matchUserSurveyResult.getSleepTime());

        // 청결도
        score += calculateCleanlinessScore(userSurveyResult.getCleanliness(), matchUserSurveyResult.getCleanliness());

        // 소음 민감도
        score += calculateNoiseSensitivityScore(userSurveyResult.getNoiseSensitivity(), matchUserSurveyResult.getNoiseSensitivity());

        // 층고
        score += calculateHeightScore(userSurveyResult.getHeight(), matchUserSurveyResult.getHeight());

        // 방 온도
        score += calculateRoomTempScore(userSurveyResult.getRoomTemp(), matchUserSurveyResult.getRoomTemp());

        // 총점 (각 항목 가중치 합)
        int totalWeight = SLEEP_TIME_WEIGHT + CLEANLINESS_WEIGHT + NOISE_SENSITIVITY_WEIGHT + HEIGHT_WEIGHT + ROOM_TEMP_WEIGHT;
        double normalizedScore = (score / totalWeight) * 100;  // 100점 만점으로 환산

        return Math.round(normalizedScore);  // 최종 점수 반올림하여 반환
    }

    // 수면 시간 점수 계산
    public double calculateSleepTimeScore(int userSleepTime, int matchUserSleepTime) {
        int diff = Math.abs(userSleepTime - matchUserSleepTime);
        return (5 - diff) * SLEEP_TIME_WEIGHT; // 차이가 적을수록 점수 증가
    }

    // 청결도 점수 계산
    public double calculateCleanlinessScore(int userCleanliness, int matchUserCleanliness) {
        int diff = Math.abs(userCleanliness - matchUserCleanliness);
        return (5 - diff) * CLEANLINESS_WEIGHT; // 차이가 적을수록 점수 증가
    }

    // 소음 민감도 점수 계산
    public double calculateNoiseSensitivityScore(int userNoiseSensitivity, int matchUserNoiseSensitivity) {
        double score = 0;

        // 두 사람이 모두 예민한 경우
        if (userNoiseSensitivity == 1 && matchUserNoiseSensitivity == 1) {
            score += NOISE_SENSITIVITY_WEIGHT; // 가점
        }
        // 한 사람만 예민한 경우
        else if (userNoiseSensitivity == 1 || matchUserNoiseSensitivity == 1) {
            score -= NOISE_SENSITIVITY_WEIGHT / 2; // 감점
        }
        // 둘 다 민감하지 않은 경우
        else if (userNoiseSensitivity == 3 && matchUserNoiseSensitivity == 3) {
            score += NOISE_SENSITIVITY_WEIGHT; // 가점
        }

        return score;
    }

    // 층고 점수 계산
    public double calculateHeightScore(int userHeight, int matchUserHeight) {
        int diff = Math.abs(userHeight - matchUserHeight);
        return (5 - diff) * HEIGHT_WEIGHT; // 차이가 적을수록 점수 증가
    }

    // 방 온도 점수 계산
    public double calculateRoomTempScore(int userRoomTemp, int matchUserRoomTemp) {
        int diff = Math.abs(userRoomTemp - matchUserRoomTemp);
        return (5 - diff) * ROOM_TEMP_WEIGHT; // 차이가 적을수록 점수 증가
    }
}
