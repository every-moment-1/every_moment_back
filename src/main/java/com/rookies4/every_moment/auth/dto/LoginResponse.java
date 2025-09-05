package com.rookies4.every_moment.auth.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        UserSummary user
) {
    public record UserSummary(Long id, String username, String email, Boolean smoking, String role) {}
}