package com.rookies4.every_moment.auth.dto;

public record RegisterResponse(
        Long id,
        String username,
        String email,
        Boolean smoking,
        String createdAt
) {}