package com.rookies4.every_moment.board.service;

import com.rookies4.every_moment.entity.UserEntity;
import com.rookies4.every_moment.board.entity.BoardLogEntity;
import com.rookies4.every_moment.board.repository.BoardLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardLogService {

    private final BoardLogRepository boardLogRepository;

    // 로그 기록 저장
    @Transactional
    public BoardLogEntity saveLog(UserEntity user, String action, String targetType, Long targetId) {
        BoardLogEntity log = BoardLogEntity.builder()
                .user(user)
                .action(action)
                .targetType(targetType)
                .targetId(targetId)
                .build();
        return boardLogRepository.save(log);
    }

    // 특정 사용자 로그 조회
    @Transactional(readOnly = true)
    public List<BoardLogEntity> getLogsByUser(Long userId) {
        return boardLogRepository.findByUserId(userId);
    }

    // 특정 타입 로그 조회
    @Transactional(readOnly = true)
    public List<BoardLogEntity> getLogsByTargetType(String targetType) {
        return boardLogRepository.findByTargetType(targetType);
    }
}