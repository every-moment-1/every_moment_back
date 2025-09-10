package com.rookies4.every_moment.service.matching;

import com.rookies4.every_moment.entity.dto.matchingDTO.MatchDTO;
import com.rookies4.every_moment.entity.dto.matchingDTO.MatchProposalDTO;
import com.rookies4.every_moment.entity.UserEntity;
import com.rookies4.every_moment.entity.dto.matchingDTO.MatchResultDTO;
import com.rookies4.every_moment.entity.matching.Match;
import com.rookies4.every_moment.entity.matching.MatchResult;
import com.rookies4.every_moment.entity.matching.MatchStatus;
import com.rookies4.every_moment.entity.matching.SurveyResult;
import com.rookies4.every_moment.repository.matching.MatchRepository;
import com.rookies4.every_moment.repository.UserRepository;
import com.rookies4.every_moment.repository.matching.MatchResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final UserRepository userRepository;
    private final ProfileService profileService;
    private final MatchRepository matchRepository;
    private final SurveyService surveyService;
    private final MatchScorerService matchScorerService;
    private final MatchResultService matchResultService;
    private final MatchResultRepository matchResultRepository;

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

    // 실제 매칭 제안 시 DB에 저장
    @Transactional
    public void proposeMatch(Long userId, MatchProposalDTO proposal) {
        Optional<UserEntity> proposer = userRepository.findById(proposal.getProposerId());
        Optional<UserEntity> targetUser = userRepository.findById(proposal.getTargetUserId());

        if (proposer.isPresent() && targetUser.isPresent()) {
            // 이미 PENDING 상태의 매칭이 존재하는지 확인
            Optional<Match> existingMatch = matchRepository.findByUser1AndUser2AndStatus(proposer.get(), targetUser.get(), MatchStatus.PENDING);

            if (existingMatch.isPresent()) {
                throw new IllegalArgumentException("이미 " + proposer.get().getUsername() + "와 " + targetUser.get().getUsername() + " 간에 PENDING 상태의 매칭이 존재합니다.");
            }

            // 새로운 매칭 생성
            Match match = new Match();
            match.setUser1(proposer.get());
            match.setUser2(targetUser.get());
            match.setScore(0); // 점수 초기화
            match.setStatus(MatchStatus.PENDING); // 상태는 기본적으로 PENDING
            matchRepository.save(match);
        } else {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
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



    // 매칭 제안 처리
    @Transactional
    public void acceptMatch(Long matchId) {
        Optional<Match> matchOptional = matchRepository.findById(matchId);

        if (matchOptional.isPresent()) {
            Match match = matchOptional.get();
            if (MatchStatus.PENDING.equals(match.getStatus())) {  // MatchStatus.PENDING으로 변경
                match.setStatus(MatchStatus.ACCEPTED);  // 상태를 ACCEPTED로 변경
                matchRepository.save(match);
            } else {
                throw new IllegalArgumentException("이미 수락된 매칭입니다.");
            }
        } else {
            throw new IllegalArgumentException("매칭을 찾을 수 없습니다.");
        }
    }

    // 매칭 거절 처리
    @Transactional
    public void rejectMatch(Long matchId) {
        Optional<Match> matchOptional = matchRepository.findById(matchId);

        if (matchOptional.isPresent()) {
            Match match = matchOptional.get();
            if (MatchStatus.PENDING.equals(match.getStatus())) {  // MatchStatus.PENDING으로 변경
                match.setStatus(MatchStatus.REJECTED);  // 상태를 REJECTED로 변경
                matchRepository.save(match);
                // 이의 제기 게시판으로 이동하도록 유도
            } else {
                throw new IllegalArgumentException("이미 거절된 매칭입니다.");
            }
        } else {
            throw new IllegalArgumentException("매칭을 찾을 수 없습니다.");
        }
    }

    // 새로운 매칭 신청 (거절된 매칭 이후)
    @Transactional
    public void requestNewMatch(Long userId, Long matchId) {
        Optional<Match> matchOptional = matchRepository.findById(matchId);

        if (matchOptional.isPresent()) {
            Match match = matchOptional.get();

            if (MatchStatus.REJECTED.equals(match.getStatus())) {  // MatchStatus.REJECTED으로 변경
                // 새로운 매칭을 제안할 수 있도록
                proposeNewMatch(userId, match.getUser2().getId());
            } else {
                throw new IllegalArgumentException("거절된 매칭만 새로운 매칭을 신청할 수 있습니다.");
            }
        } else {
            throw new IllegalArgumentException("매칭을 찾을 수 없습니다.");
        }
    }

    // 스왑 신청 처리 (새로운 매칭 상태인 SWAP_REQUESTED로 변경)
    @Transactional
    public void swapMatch(Long matchId) {
        Optional<Match> matchOptional = matchRepository.findById(matchId);

        if (matchOptional.isPresent()) {
            Match match = matchOptional.get();

            // 매칭 상태가 "PENDING"일 때도 스왑 신청을 받을 수 있도록 처리
            if (MatchStatus.PENDING.equals(match.getStatus())) {  // MatchStatus.PENDING 상태로도 스왑 신청 가능
                match.setStatus(MatchStatus.SWAP_REQUESTED);  // 스왑 신청 상태로 변경
                matchRepository.save(match);  // 변경된 매칭 저장

                // 관리자가 새로운 매칭을 제안할 수 있도록
                proposeNewMatch(match.getUser1().getId(), match.getUser2().getId());
            } else {
                throw new IllegalArgumentException("매칭 상태가 PENDING이 아니면 스왑을 신청할 수 없습니다.");
            }
        } else {
            throw new IllegalArgumentException("매칭을 찾을 수 없습니다.");
        }
    }

    // 관리자가 새로운 매칭을 제안할 수 있도록 하는 메소드 (스왑 신청 후에만 가능)
    @Transactional
    public void proposeNewMatch(Long proposerId, Long targetUserId) {
        Optional<UserEntity> proposer = userRepository.findById(proposerId);
        Optional<UserEntity> targetUser = userRepository.findById(targetUserId);

        if (proposer.isPresent() && targetUser.isPresent()) {
            // 기존 매칭을 찾음 (스왑 신청 후 새로운 매칭 제안)
            List<Match> existingMatches = matchRepository.findByUser1_IdAndUser2_Id(proposer.get().getId(), targetUser.get().getId());

            // 기존 매칭 상태가 SWAP_REQUESTED일 때만 새로운 매칭 제안 가능
            for (Match match : existingMatches) {
                if (MatchStatus.SWAP_REQUESTED.equals(match.getStatus())) {  // MatchStatus.SWAP_REQUESTED로 변경
                    // 새로운 매칭을 제안
                    Match newMatch = new Match();
                    newMatch.setUser1(proposer.get());
                    newMatch.setUser2(targetUser.get());
                    newMatch.setScore(0);  // 매칭 점수 초기화
                    newMatch.setStatus(MatchStatus.PENDING);  // 상태를 PENDING으로 설정

                    // 새 매칭을 데이터베이스에 저장
                    matchRepository.save(newMatch);
                    return;  // 매칭 생성 후 종료
                }
            }

            // 만약 SWAP_REQUESTED 상태의 매칭이 없다면 예외 발생
            throw new IllegalArgumentException("스왑 신청이 완료된 매칭만 새로운 매칭을 제안할 수 있습니다.");
        } else {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
    }
}