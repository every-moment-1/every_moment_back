package com.rookies4.every_moment.security.jwt;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.rookies4.every_moment.exception.ApiErrorResponse;
import com.rookies4.every_moment.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final ObjectMapper om = new ObjectMapper();
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         org.springframework.security.core.AuthenticationException authException) throws IOException {
        var body = ApiErrorResponse.of(ErrorCode.INVALID_CREDENTIALS, null, null);
        response.setStatus(ErrorCode.INVALID_CREDENTIALS.status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        om.writeValue(response.getOutputStream(), body);
    }
}