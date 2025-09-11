package com.rookies4.every_moment.board.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PostDetail(
        Long id,
        String category,
        String title,
        String content,
        LocalDateTime createdAt,
        String authorName,
        List<CommentItem> comments
) {}
