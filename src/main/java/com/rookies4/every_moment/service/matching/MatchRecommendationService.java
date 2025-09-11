package com.rookies4.every_moment.service.matching;

import com.rookies4.every_moment.entity.dto.matchingDTO.MatchRecommendationDTO;
import com.rookies4.every_moment.entity.UserEntity;
import com.rookies4.every_moment.entity.matching.Match;
import com.rookies4.every_moment.entity.matching.MatchRecommendation;
import com.rookies4.every_moment.entity.matching.MatchStatus;
import com.rookies4.every_moment.entity.matching.SurveyResult;
import com.rookies4.every_moment.repository.matching.MatchRecommendationRepository;
import com.rookies4.every_moment.repository.matching.MatchRepository;
import com.rookies4.every_moment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchRecommendationService {

    private final UserRepository userRepository;
    private final ProfileService profileService;
    private final SurveyService surveyService;
    private final MatchScorerService matchScorerService;
    private final MatchResultService matchResultService;
    private final MatchRecommendationRepository matchRecommendationRepository;


    @Transactional
    public List<MatchRecommendationDTO> getMatchingRecommendations(UserEntity user) {
        // 사용자 설문 결과를 가져옴
        SurveyResult userSurveyResult = surveyService.getSurveyResult(user.getId());

        // 회원가입한 사용자 정보에서 성별과 흡연 여부를 1차 필터링 조건으로 사용
        Integer gender = user.getGender();
        Boolean smoking = user.getSmoking();

        // 필터링된 사용자 리스트 가져오기 (여성-흡연, 여성-비흡연, 남성-흡연, 남성-비흡연)
        List<UserEntity> filteredUsers = profileService.filterUsersByProfile(gender, smoking);

        // 필터링된 사용자들의 설문 결과를 한 번에 배치로 조회
        List<Long> userIds = filteredUsers.stream().map(UserEntity::getId).collect(Collectors.toList());
        List<SurveyResult> matchUserSurveyResults = surveyService.getSurveyResultsByUserIds(userIds); // 배치 조회

        List<MatchRecommendationDTO> recommendations = new ArrayList<>();
        for (UserEntity matchUser : filteredUsers) {
            if (!matchUser.getId().equals(user.getId())) {
                // 사용자 설문 결과 찾기
                SurveyResult matchUserSurveyResult = matchUserSurveyResults.stream()
                        .filter(survey -> survey.getUser().getId().equals(matchUser.getId()))
                        .findFirst()
                        .orElse(null);

                // 매칭 점수 계산
                double score = matchScorerService.calculateScore(userSurveyResult, matchUserSurveyResult);

                // 선호도 점수 계산 (추가) 후 0~100 범위로 환산
                double preferenceScore = matchScorerService.calculatePreferenceScore(userSurveyResult, matchUserSurveyResult);

                // 매칭 이유 리스트 생성
                List<String> matchReasons = matchResultService.generateMatchReasons(userSurveyResult, matchUserSurveyResult);

                // 추천 DTO에 추가
                MatchRecommendationDTO recommendationDTO = new MatchRecommendationDTO(
                        matchUser.getId(),
                        "익명 사용자",
                        (int) score,
                        "PENDING",
                        matchReasons,
                        "익명 룸메이트",
                        preferenceScore  // 0~100 범위로 변경된 선호도 점수 추가
                );
                recommendations.add(recommendationDTO);

                // DB에 추천 결과 저장 (MatchRecommendation 엔티티)
                MatchRecommendation matchRecommendation = new MatchRecommendation();
                matchRecommendation.setUser(user);
                matchRecommendation.setUsername("익명 사용자");
                matchRecommendation.setScore((int) score);
                matchRecommendation.setStatus("PENDING");
                matchRecommendation.setMatchReasons(matchReasons);
                matchRecommendation.setRoommateName("익명 룸메이트");
                matchRecommendation.setPreferenceScore(preferenceScore);  // 0~100 범위로 저장된 선호도 점수 저장

                // 추천 결과를 DB에 저장
                matchRecommendationRepository.save(matchRecommendation);
            }
        }

        // 점수 높은 순으로 정렬
        recommendations.sort((m1, m2) -> Double.compare(m2.getScore(), m1.getScore()));

        // 상위 10개만 반환
        return recommendations.stream().limit(10).collect(Collectors.toList());
    }


    // 1:1 룸메이트 추천 저장
    @Transactional
    public void saveMatchRecommendation(MatchRecommendationDTO recommendationDTO) {
        // MatchRecommendation 엔티티 객체 생성
        MatchRecommendation matchRecommendation = new MatchRecommendation();

        // 사용자 엔티티를 가져오고, 추천된 사용자 정보를 설정
        matchRecommendation.setUser(userRepository.findById(recommendationDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("추천할 사용자가 없습니다.")));

        // DTO에서 받은 정보를 엔티티에 설정
        matchRecommendation.setUsername(recommendationDTO.getUsername());
        matchRecommendation.setScore(recommendationDTO.getScore());
        matchRecommendation.setStatus("PENDING");  // 기본 상태로 PENDING 설정
        matchRecommendation.setMatchReasons(recommendationDTO.getMatchReasons());
        matchRecommendation.setRoommateName(recommendationDTO.getRoommateName());
        matchRecommendation.setPreferenceScore(recommendationDTO.getPreferenceScore());

        // 엔티티를 DB에 저장
        matchRecommendationRepository.save(matchRecommendation);
    }

    // 1:1 룸메이트 추천 조회
    public Optional<MatchRecommendationDTO> getMatchingRecommendation(UserEntity user) {
        MatchRecommendation matchRecommendation = matchRecommendationRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("추천된 룸메이트가 없습니다."));

        return Optional.of(new MatchRecommendationDTO(
                matchRecommendation.getUser().getId(),
                matchRecommendation.getUsername(),
                matchRecommendation.getScore(),
                matchRecommendation.getStatus(),
                matchRecommendation.getMatchReasons(),
                matchRecommendation.getRoommateName(),
                matchRecommendation.getPreferenceScore()
        ));
    }

}
