package com.rookies4.every_moment.match.service;

import com.rookies4.every_moment.match.entity.SurveyResult;
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


    // MatchController 유사도 점수 계산
    // 수면 시간 유사도 계산
    public double calculateSleepTimeSimilarity(int user1SleepTime, int user2SleepTime) {
        // 차이를 구하고 이를 정규화 (0 ~ 1 사이)
        int diff = Math.abs(user1SleepTime - user2SleepTime);
        return 1 - (double) diff / 3.0;  // 예를 들어, 최대 차이는 3까지로 설정
    }

    // 청결도 유사도 계산
    public double calculateCleanlinessSimilarity(int user1Cleanliness, int user2Cleanliness) {
        int diff = Math.abs(user1Cleanliness - user2Cleanliness);
        return 1 - (double) diff / 3.0;  // 예시로 최대 차이는 3
    }

    // 소음 민감도 유사도 계산
    public double calculateNoiseSensitivitySimilarity(int user1NoiseSensitivity, int user2NoiseSensitivity) {
        return user1NoiseSensitivity == user2NoiseSensitivity ? 1.0 : 0.0;
    }

    // 층고 유사도 계산
    public double calculateHeightSimilarity(int user1Height, int user2Height) {
        int diff = Math.abs(user1Height - user2Height);
        return 1 - (double) diff / 2.0;  // 예시로 최대 차이는 2
    }

    // 방 온도 유사도 계산
    public double calculateRoomTempSimilarity(int user1RoomTemp, int user2RoomTemp) {
        int diff = Math.abs(user1RoomTemp - user2RoomTemp);
        return 1 - (double) diff / 2.0;  // 예시로 최대 차이는 2
    }

    // 두 사용자의 유사도 계산
    public double calculateSimilarity(SurveyResult user1Survey, SurveyResult user2Survey) {
        double sleepTimeSimilarity = calculateSleepTimeSimilarity(user1Survey.getSleepTime(), user2Survey.getSleepTime());
        double cleanlinessSimilarity = calculateCleanlinessSimilarity(user1Survey.getCleanliness(), user2Survey.getCleanliness());
        double noiseSensitivitySimilarity = calculateNoiseSensitivitySimilarity(user1Survey.getNoiseSensitivity(), user2Survey.getNoiseSensitivity());
        double heightSimilarity = calculateHeightSimilarity(user1Survey.getHeight(), user2Survey.getHeight());
        double roomTempSimilarity = calculateRoomTempSimilarity(user1Survey.getRoomTemp(), user2Survey.getRoomTemp());

        // 각 항목의 유사도를 평균하여 최종 유사도 계산
        return (sleepTimeSimilarity + cleanlinessSimilarity + noiseSensitivitySimilarity + heightSimilarity + roomTempSimilarity) / 5.0;
    }



    // Recommendation에 계산

    public double calculatePreferenceScore(SurveyResult userSurveyResult, SurveyResult matchUserSurveyResult) {
        // 수면 시간, 청결도, 소음 민감도, 층고, 방 온도 등의 항목을 바탕으로 점수를 계산
        double score = 0;

        // 예시로 수면 시간과 청결도의 차이를 바탕으로 선호도 점수 계산
        score += calculateSleepTimeScore(userSurveyResult.getSleepTime(), matchUserSurveyResult.getSleepTime());
        score += calculateCleanlinessScore(userSurveyResult.getCleanliness(), matchUserSurveyResult.getCleanliness());

        // 정규화 (0 ~ 1 범위로)
        int totalWeight = SLEEP_TIME_WEIGHT + CLEANLINESS_WEIGHT; // 사용한 항목들의 가중치 합
        double normalizedPreferenceScore = (score / totalWeight); // 점수 합을 총 가중치로 나누어 0~1 범위로 정규화

//        return Math.min(normalizedPreferenceScore, 1.0); // 1.0을 넘지 않도록 제한
        // 100점 만점으로 환산
        return Math.min(normalizedPreferenceScore * 100, 100.0); // 100점 만점으로 제한
    }

}
