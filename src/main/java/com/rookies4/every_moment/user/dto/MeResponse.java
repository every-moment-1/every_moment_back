package com.rookies4.every_moment.user.dto;

public record MeResponse(
        Long id,
        String username,
        String email,
        Boolean smoking,
        String role,
        Boolean active,
        String createdAt
) {}