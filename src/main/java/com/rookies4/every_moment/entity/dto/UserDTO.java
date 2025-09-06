package com.rookies4.every_moment.entity.dto;

public record UserDTO(
        Long id,
        String username,
        String email,
        Boolean smoking,
        String role,
        Boolean active,
        String createdAt
) {}