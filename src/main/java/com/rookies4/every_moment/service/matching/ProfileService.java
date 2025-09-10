package com.rookies4.every_moment.service.matching;

import com.rookies4.every_moment.entity.UserEntity;
import com.rookies4.every_moment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;

    // 성별 (Integer)과 흡연 여부 (Boolean)을 기준으로 필터링된 사용자 리스트를 반환
    public List<UserEntity> filterUsersByProfile(Integer gender, Boolean smoking) {
        // UserRepository에서 성별과 흡연 여부로 필터링된 사용자 목록을 가져옴
        return userRepository.findByGenderAndSmoking(gender, smoking);
    }

}