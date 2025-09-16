package com.rookies4.every_moment.board.dto;

import java.time.LocalDateTime;

public record PostListItem(
        Long id,
        String category,
        String title,
        LocalDateTime createdAt,
        String authorName,
        String status
){}
