package com.rookies4.every_moment.board.dto;

import java.time.LocalDateTime;

public record CommentItem(
        Long id,
        String content,
        Long authorId,
        String authorName,
        LocalDateTime createdAt
) {}
