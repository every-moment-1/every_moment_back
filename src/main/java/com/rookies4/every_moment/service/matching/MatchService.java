package com.rookies4.every_moment.service.matching;

import com.rookies4.every_moment.entity.dto.matchingDTO.MatchProposalDTO;
import com.rookies4.every_moment.entity.UserEntity;
import com.rookies4.every_moment.entity.matching.Match;
import com.rookies4.every_moment.entity.matching.MatchStatus;
import com.rookies4.every_moment.repository.matching.MatchRepository;
import com.rookies4.every_moment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final UserRepository userRepository;
    private final MatchRepository matchRepository;


    // 실제 매칭 제안 시 DB에 저장
    @Transactional
    public Long proposeMatch(Long userId, MatchProposalDTO proposal) {
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

            // 매칭 ID를 반환
            return match.getId(); // 매칭 ID를 반환
        } else {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
    }


    // 매칭 수락 처리
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