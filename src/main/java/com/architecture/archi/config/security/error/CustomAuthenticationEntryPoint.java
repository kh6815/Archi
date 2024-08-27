package com.architecture.archi.config.security.error;

import com.architecture.archi.common.error.ExceptionCode;
import com.architecture.archi.common.model.ApiResponseModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

// 인증이 필요한 리소스에 인증되지 않은 사용자가 접근할 때 호출
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ExceptionCode exceptionCode = ExceptionCode.UNAUTHORIZED;

//        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setStatus(exceptionCode.getStatusCode().value());
        response.setContentType("application/json");
        ApiResponseModel apiResponse = new ApiResponseModel(exceptionCode.getResultCode(), exceptionCode.getResultMessage());
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}