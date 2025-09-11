package com.rookies4.every_moment.board.controller;

import com.rookies4.every_moment.entity.UserEntity;
import com.rookies4.every_moment.board.entity.BoardLogEntity;
import com.rookies4.every_moment.service.UserService;
import com.rookies4.every_moment.board.service.BoardLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/board-logs")
@RequiredArgsConstructor
public class BoardLogController {

    private final BoardLogService boardLogService;
    private final UserService userService;

    // 로그 기록 저장
    @PostMapping
    public BoardLogEntity saveLog(@RequestParam String action,
                                  @RequestParam String targetType,
                                  @RequestParam Long targetId,
                                  Authentication auth) {
        UserEntity user = userService.getCurrentUser(auth);
        return boardLogService.saveLog(user, action, targetType, targetId);
    }

    // 특정 사용자 로그 조회
    @GetMapping("/user/{userId}")
    public List<BoardLogEntity> getLogsByUser(@PathVariable Long userId) {
        return boardLogService.getLogsByUser(userId);
    }

    // 특정 타입 로그 조회
    @GetMapping("/type/{targetType}")
    public List<BoardLogEntity> getLogsByTargetType(@PathVariable String targetType) {
        return boardLogService.getLogsByTargetType(targetType);
    }
}
