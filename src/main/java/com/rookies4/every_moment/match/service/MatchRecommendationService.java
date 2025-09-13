package com.rookies4.every_moment.match.service;

import com.rookies4.every_moment.match.entity.dto.MatchRecommendationDTO;
import com.rookies4.every_moment.entity.UserEntity;
import com.rookies4.every_moment.match.entity.MatchRecommendation;
import com.rookies4.every_moment.match.entity.SurveyResult;
import com.rookies4.every_moment.match.repository.MatchRecommendationRepository;
import com.rookies4.every_moment.match.repository.MatchResultRepository;
import com.rookies4.every_moment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchRecommendationService {

    private final ProfileService profileService;
    private final SurveyService surveyService;
    private final MatchScorerService matchScorerService;
    private final MatchResultService matchResultService;
    private final MatchRecommendationRepository matchRecommendationRepository;
    private final UserRepository userRepository;
    private final MatchResultRepository matchResultRepository;

    @Transactional
    public List<MatchRecommendationDTO> getMatchingRecommendations(Long userId) {
        // 1. 사용자의 userId를 통해 UserEntity 객체를 찾습니다.
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 사용자 설문 결과를 가져옵니다.
        SurveyResult userSurveyResult = surveyService.getSurveyResult(user.getId());

        // 3. 1차 필터링된 사용자 리스트를 가져옵니다.
        List<UserEntity> filteredUsers = profileService.filterUsersByProfile(user.getGender(), user.getSmoking());

        // 4. 필터링된 사용자들의 설문 결과를 HashMap으로 효율적으로 조회합니다.
        List<Long> userIds = filteredUsers.stream().map(UserEntity::getId).collect(Collectors.toList());
        Map<Long, SurveyResult> surveyResultMap = surveyService.getSurveyResultsByUserIds(userIds).stream()
                .collect(Collectors.toMap(survey -> survey.getUser().getId(), Function.identity()));

        List<MatchRecommendationDTO> recommendations = new ArrayList<>();

        // 5. 추천 점수를 계산하고 DTO 리스트를 만듭니다.
        for (UserEntity matchUser : filteredUsers) {
            if (!matchUser.getId().equals(user.getId())) {
                SurveyResult matchUserSurveyResult = surveyResultMap.get(matchUser.getId());
                if (matchUserSurveyResult == null) {
                    continue; // 설문 결과가 없으면 건너뜀
                }

                double score = matchScorerService.calculateScore(userSurveyResult, matchUserSurveyResult);
                double preferenceScore = matchScorerService.calculatePreferenceScore(userSurveyResult, matchUserSurveyResult);

                MatchRecommendationDTO recommendationDTO = new MatchRecommendationDTO(
                        matchUser.getId(), "익명 사용자", (int) score, "PENDING", "익명 룸메이트", preferenceScore
                );
                recommendations.add(recommendationDTO);
            }
        }

        // 6. 점수가 높은 순으로 정렬하고 상위 10개만 선택합니다.
        List<MatchRecommendationDTO> top10Recommendations = recommendations.stream()
                .sorted((m1, m2) -> Double.compare(m2.getScore(), m1.getScore()))
                .limit(10)
                .collect(Collectors.toList());

        // 7. 상위 10개 추천 목록만 DB에 저장하기 위한 엔티티 리스트를 만듭니다.
        List<MatchRecommendation> matchRecommendations = new ArrayList<>();
        for (MatchRecommendationDTO dto : top10Recommendations) {
            MatchRecommendation entity = new MatchRecommendation();
            entity.setUser(user);
            entity.setUsername(dto.getUsername());
            entity.setScore(dto.getScore());
            entity.setStatus(dto.getStatus());
            entity.setRoommateName(dto.getRoommateName());
            entity.setPreferenceScore(dto.getPreferenceScore());
            matchRecommendations.add(entity);
        }

        // 8. DB에 한 번에 저장합니다 (배치 저장).
        matchRecommendationRepository.saveAll(matchRecommendations);

        // 9. 상위 10개 추천 목록만 반환합니다.
        return top10Recommendations;
    }

}



