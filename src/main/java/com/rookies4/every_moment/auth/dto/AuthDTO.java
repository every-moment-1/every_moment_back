package com.rookies4.every_moment.auth.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 모든 인증 관련 DTO를 한 파일로 모은 컨테이너.
 * 외부에서는 AuthDtos.RegisterRequest 같이 사용합니다.
 */
public final class AuthDTO {
    private AuthDTO() {}

    // ===== Register =====
    public record RegisterRequest(
            @NotBlank @Pattern(regexp = "^[a-zA-Z0-9_]{3,20}$", message = "username 형식 오류")
            String username,
            @NotBlank @Email
            String email,
            @NotBlank @Size(min = 8, message = "비밀번호는 8자 이상")
            String password,
            Boolean smoking
    ) {}

    public record RegisterResponse(
            Long id,
            String username,
            String email,
            Boolean smoking,
            String createdAt
    ) {}

    // ===== Login =====
    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {}

    public record LoginResponse(
            String accessToken,
            String refreshToken,
            UserSummary user
    ) {
        public record UserSummary(Long id, String username, String email, Boolean smoking, String role) {}
    }

    // ===== Refresh =====
    public record RefreshRequest(
            @NotBlank String refreshToken
    ) {}

    public record RefreshResponse(
            String accessToken,
            String refreshToken
    ) {}
}