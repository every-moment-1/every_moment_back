package com.rookies4.every_moment.match.service;

import com.rookies4.every_moment.match.entity.SurveyResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Getter
@AllArgsConstructor
public class MatchScorerService {

    public static final int SLEEP_TIME_WEIGHT = 40;
    public static final int CLEANLINESS_WEIGHT = 18;
    public static final int NOISE_SENSITIVITY_WEIGHT = 18;
    public static final int HEIGHT_WEIGHT = 10;
    public static final int ROOM_TEMP_WEIGHT = 14;

    // 점수 계산 (전체 항목에 대해 가중치를 적용한 점수 계산)
    public double calculateScore(SurveyResult userSurveyResult, SurveyResult matchUserSurveyResult) {
        double score = 0;

        // 각 항목의 유사도 계산 후 가중치를 적용한 점수 계산
        score += calculateScoreWithWeight(calculateSleepTimeSimilarity(userSurveyResult.getSleepTime(), matchUserSurveyResult.getSleepTime()), SLEEP_TIME_WEIGHT);
        score += calculateScoreWithWeight(calculateCleanlinessSimilarity(userSurveyResult.getCleanliness(), matchUserSurveyResult.getCleanliness()), CLEANLINESS_WEIGHT);
        score += calculateScoreWithWeight(calculateNoiseSensitivitySimilarity(userSurveyResult.getNoiseSensitivity(), matchUserSurveyResult.getNoiseSensitivity()), NOISE_SENSITIVITY_WEIGHT);
        score += calculateScoreWithWeight(calculateHeightSimilarity(userSurveyResult.getHeight(), matchUserSurveyResult.getHeight()), HEIGHT_WEIGHT);
        score += calculateScoreWithWeight(calculateRoomTempSimilarity(userSurveyResult.getRoomTemp(), matchUserSurveyResult.getRoomTemp()), ROOM_TEMP_WEIGHT);

        // 각 항목 가중치 합
        int totalWeight = SLEEP_TIME_WEIGHT + CLEANLINESS_WEIGHT + NOISE_SENSITIVITY_WEIGHT + HEIGHT_WEIGHT + ROOM_TEMP_WEIGHT;

        // 정규화 (0~100 범위로 환산)
        double normalizedScore = (score / totalWeight);  // 0~1 사이로 계산된 점수

        // 최종 점수 반환 (100점 만점으로 반올림하여 반환)
        return Math.round(normalizedScore * 100);
    }

    // 점수 계산을 위한 helper 메서드 (유사도 * 가중치)
    private double calculateScoreWithWeight(double similarity, int weight) {
        return similarity * weight;
    }

    // 수면 시간 유사도 계산 (0 ~ 1 사이로 정규화)
    public double calculateSleepTimeSimilarity(int user1SleepTime, int user2SleepTime) {
        int diff = Math.abs(user1SleepTime - user2SleepTime);
        return 1 - (double) diff / 5.0;  // 수면 시간 차이 최대값을 5로 설정하여 유사도를 계산
    }

    // 청결도 유사도 계산 (0 ~ 1 사이로 정규화)
    public double calculateCleanlinessSimilarity(int user1Cleanliness, int user2Cleanliness) {
        int diff = Math.abs(user1Cleanliness - user2Cleanliness);
        return 1 - (double) diff / 5.0;  // 최대 차이를 5로 설정하여 유사도를 계산
    }

    // 소음 민감도 유사도 계산 (0 ~ 1 사이로 정규화)
    public double calculateNoiseSensitivitySimilarity(int user1NoiseSensitivity, int user2NoiseSensitivity) {
        return user1NoiseSensitivity == user2NoiseSensitivity ? 1.0 : 0.0;
    }

    // 층고 유사도 계산 (0 ~ 1 사이로 정규화)
    public double calculateHeightSimilarity(int user1Height, int user2Height) {
        int diff = Math.abs(user1Height - user2Height);
        return 1 - (double) diff / 2.0;  // 최대 차이를 2로 설정하여 유사도를 계산
    }

    // 방 온도 유사도 계산 (0 ~ 1 사이로 정규화)
    public double calculateRoomTempSimilarity(int user1RoomTemp, int user2RoomTemp) {
        int diff = Math.abs(user1RoomTemp - user2RoomTemp);
        return 1 - (double) diff / 3.0;  // 최대 차이를 3으로 설정하여 유사도를 계산
    }

    // 두 사용자의 유사도 계산 (평균 유사도 계산)
    public double calculateSimilarity(SurveyResult user1Survey, SurveyResult user2Survey) {
        double sleepTimeSimilarity = calculateSleepTimeSimilarity(user1Survey.getSleepTime(), user2Survey.getSleepTime());
        double cleanlinessSimilarity = calculateCleanlinessSimilarity(user1Survey.getCleanliness(), user2Survey.getCleanliness());
        double noiseSensitivitySimilarity = calculateNoiseSensitivitySimilarity(user1Survey.getNoiseSensitivity(), user2Survey.getNoiseSensitivity());
        double heightSimilarity = calculateHeightSimilarity(user1Survey.getHeight(), user2Survey.getHeight());
        double roomTempSimilarity = calculateRoomTempSimilarity(user1Survey.getRoomTemp(), user2Survey.getRoomTemp());

        // 각 항목의 유사도를 평균하여 최종 유사도 계산
        double similarity = (sleepTimeSimilarity + cleanlinessSimilarity + noiseSensitivitySimilarity + heightSimilarity + roomTempSimilarity) / 5.0;

        // 소수점 둘째 자리까지 반올림하여 반환
        BigDecimal roundedSimilarity = new BigDecimal(similarity).setScale(2, RoundingMode.HALF_UP);

        return roundedSimilarity.doubleValue();  // 최종적으로 소수점 둘째 자리까지 반환
    }

    // Recommendation에 계산
    public double calculatePreferenceScore(SurveyResult userSurveyResult, SurveyResult matchUserSurveyResult) {
        // 점수와 이유를 묶어서 관리하는 클래스
        class SimilarityResult {
            private final BigDecimal score;
            private final String reason;

            public SimilarityResult(BigDecimal score, String reason) {
                this.score = score;
                this.reason = reason;
            }

            public BigDecimal getScore() {
                return score;
            }

            public String getReason() {
                return reason;
            }
        }

        List<SimilarityResult> allSimilarities = new ArrayList<>();

        // 1. 각 유사도 점수를 계산하고, 소수점 둘째 자리에서 반올림
        BigDecimal sleepTimeSimilarity = new BigDecimal(1 - Math.abs(userSurveyResult.getSleepTime() - matchUserSurveyResult.getSleepTime()) / 3.0).setScale(2, RoundingMode.HALF_UP);
        allSimilarities.add(new SimilarityResult(sleepTimeSimilarity, "취침/기상 유사: " + sleepTimeSimilarity.toPlainString()));

        BigDecimal cleanlinessSimilarity = new BigDecimal(1 - Math.abs(userSurveyResult.getCleanliness() - matchUserSurveyResult.getCleanliness()) / 4.0).setScale(2, RoundingMode.HALF_UP);
        allSimilarities.add(new SimilarityResult(cleanlinessSimilarity, "청결도 유사: " + cleanlinessSimilarity.toPlainString()));

        BigDecimal noiseSensitivitySimilarity = new BigDecimal(1 - Math.abs(userSurveyResult.getNoiseSensitivity() - matchUserSurveyResult.getNoiseSensitivity()) / 3.0).setScale(2, RoundingMode.HALF_UP);
        allSimilarities.add(new SimilarityResult(noiseSensitivitySimilarity, "소음 민감도 차이: " + noiseSensitivitySimilarity.toPlainString()));

        BigDecimal heightSimilarity = new BigDecimal(1 - Math.abs(userSurveyResult.getHeight() - matchUserSurveyResult.getHeight()) / 3.0).setScale(2, RoundingMode.HALF_UP);
        allSimilarities.add(new SimilarityResult(heightSimilarity, "층고 유사: " + heightSimilarity.toPlainString()));

        BigDecimal roomTempSimilarity = new BigDecimal(1 - Math.abs(userSurveyResult.getRoomTemp() - matchUserSurveyResult.getRoomTemp()) / 3.0).setScale(2, RoundingMode.HALF_UP);
        allSimilarities.add(new SimilarityResult(roomTempSimilarity, "방 온도 유사: " + roomTempSimilarity.toPlainString()));

        // 2. 반올림된 점수를 내림차순 정렬
        allSimilarities.sort(Comparator.comparing(SimilarityResult::getScore).reversed());

        // 3. 상위 3개 항목만 선택
        List<SimilarityResult> top3Similarities = allSimilarities.stream()
                .limit(3)
                .collect(Collectors.toList());

        // 4. 반올림된 상위 3개 점수를 사용해 최종 점수 계산
        BigDecimal sum = top3Similarities.stream()
                .map(SimilarityResult::getScore)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal finalScore = sum.divide(new BigDecimal("3"), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);

        // 상위 3개 이유만 반환
        List<String> matchReasons = top3Similarities.stream()
                .map(SimilarityResult::getReason)
                .collect(Collectors.toList());

        // 최종 점수 100점 만점으로 환산
        double finalPreferenceScore = finalScore.doubleValue();

        // 반환된 값이 100을 넘지 않도록 제한
        return Math.min(finalPreferenceScore, 100.0); // 100점 만점으로 제한
    }
}
