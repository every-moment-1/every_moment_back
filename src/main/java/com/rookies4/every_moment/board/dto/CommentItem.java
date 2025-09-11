package com.rookies4.every_moment.board.dto;

import java.time.LocalDateTime;

public record CommentItem(
        Long id,
        String content,
        String authorName,
        LocalDateTime createdAt
) {}
