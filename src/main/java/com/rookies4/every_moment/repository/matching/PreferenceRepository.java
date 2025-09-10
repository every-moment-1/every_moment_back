package com.rookies4.every_moment.repository.matching;

import com.rookies4.every_moment.entity.matching.Preference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreferenceRepository extends JpaRepository<Preference, Long> {

    // 사용자의 선호도를 조회할 수 있는 메서드 (userId로 조회)
    Preference findByUserId(Long userId);
}