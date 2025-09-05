package com.rookies4.every_moment.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Pattern(regexp = "^[a-zA-Z0-9_]{3,20}$", message = "username 형식 오류")
        String username,
        @NotBlank @Email
        String email,
        @NotBlank @Size(min=8, message="비밀번호는 8자 이상")
        String password,
        Boolean smoking
) {}