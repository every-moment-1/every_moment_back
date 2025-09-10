package com.rookies4.every_moment.service.matching;

import com.rookies4.every_moment.entity.dto.matchingDTO.MatchDTO;
import com.rookies4.every_moment.entity.UserEntity;
import com.rookies4.every_moment.entity.matching.Match;
import com.rookies4.every_moment.entity.matching.MatchStatus;
import com.rookies4.every_moment.entity.matching.SurveyResult;
import com.rookies4.every_moment.repository.matching.MatchRepository;
import com.rookies4.every_moment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchRecommendationService {

    private final UserRepository userRepository;
    private final ProfileService profileService;
    private final MatchRepository matchRepository;
    private final SurveyService surveyService;
    private final MatchScorerService matchScorerService;
    private final MatchResultService matchResultService;


    // 추천된 사용자 목록을 가져오기 위한 매칭 함수 (추천 결과 DB 저장 포함)
    public List<MatchDTO> getMatchingRecommendations(UserEntity user) {
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

        List<MatchDTO> recommendations = new ArrayList<>();
        for (UserEntity matchUser : filteredUsers) {
            if (!matchUser.getId().equals(user.getId())) {
                // 사용자 설문 결과 찾기
                SurveyResult matchUserSurveyResult = matchUserSurveyResults.stream()
                        .filter(survey -> survey.getUser().getId().equals(matchUser.getId()))
                        .findFirst()
                        .orElse(null);
                double score = matchScorerService.calculateScore(userSurveyResult, matchUserSurveyResult);

                // 매칭 이유 리스트 생성
                List<String> matchReasons = matchResultService.generateMatchReasons(userSurveyResult, matchUserSurveyResult);

                // 추천 DTO에 추가
                recommendations.add(new MatchDTO(
                        matchUser.getId(),
                        "익명 사용자",
                        (int) score,
                        "PENDING",
                        matchReasons,
                        "익명 룸메이트",
                        score
                ));
            }
        }
        // 점수 높은 순으로 정렬
        recommendations.sort((m1, m2) -> Double.compare(m2.getScore(), m1.getScore()));

        // 상위 10개만 반환
        return recommendations.stream().limit(10).collect(Collectors.toList());
    }


    // 1:1 룸메이트 추천만 반환 (추천 결과 DB 저장 포함)
    public MatchDTO getMatchingRecommendation(UserEntity user) {
        // 추천 목록을 DB에 저장
        List<MatchDTO> recommendations = getMatchingRecommendations(user);

        // 첫 번째 추천을 반환하기 전에 DB에 저장된 매칭을 한 번 더 저장
        MatchDTO firstRecommendation = recommendations.stream().findFirst().orElse(null);

        if (firstRecommendation != null) {
            // 첫 번째 추천 결과를 DB에 저장
            Match match = new Match();
            match.setUser1(user);  // 제안자
            match.setUser2(userRepository.findById(firstRecommendation.getUserId()).orElse(null));  // 대상자
            match.setScore(firstRecommendation.getScore());  // 매칭 점수
            match.setStatus(MatchStatus.PENDING);  // 상태는 기본적으로 PENDING
            matchRepository.save(match); // DB에 매칭 결과 저장
        }

        return firstRecommendation;
    }
}
