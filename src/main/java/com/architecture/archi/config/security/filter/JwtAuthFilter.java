package com.architecture.archi.config.security.filter;

import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.error.ExceptionCode;
import com.architecture.archi.common.model.ApiResponseModel;
import com.architecture.archi.config.security.JwtUtils;
import com.architecture.archi.config.security.user.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter { // OncePerRequestFilter -> 한 번 실행 보장

    // SecurityConfig에서 생성자로 주입
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtils jwtUtils;
    private final RedisTemplate<String, Object> accessTokenBlackListTemplate;

    // 회원가입과 로그인등의 url은 filter을 거치지 않게 해야함. ->
    // ex) 헤더에 토큰이 있는 채로 해당 필터를 걸치면 에러가 나기 때문에
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        String[] excludePath = {"/api/v1/user/signup",
                "/api/v1/user/check-id",
                "/api/v1/user/check-email",
                "/api/v1/user/check-nickname",
                "/api/v1/user/init-password",
                "/api/v1/auth/**",
                "/api/v1/content/list/**",
//                "/api/v1/content/get/**",
                "/api/v1/comment/list/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/v3/api-docs/**",
                "/api-docs",
                "/swagger-ui/index.html",
                "/index.html",
                "/login/oauth2/code/**",
                "/oauth-login/login",};
        String path = request.getRequestURI();
        return Arrays.stream(excludePath).anyMatch(path::startsWith);
    }

    @Override
    /**
     * JWT 토큰 검증 필터 수행
     */
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        //JWT가 헤더에 있는 경우
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = request.getHeader("Authorization").replace("Bearer ", "");

            // blackList 명단에 있는 토큰인지 확인
            if (!accessTokenBlackListTemplate.hasKey(token)) {
                //JWT 유효성 검증
                if (jwtUtils.validateToken(token)) {
                    String userId = jwtUtils.getUserIdFromToken(token);

                    //유저와 토큰 일치 시 userDetails 생성
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId);

                    if (userDetails != null) {
                        //UserDetsils, Password, Role -> 접근권한 인증 Token 생성
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                        //현재 Request의 Security Context에 접근권한 설정
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
                } else {
                    // 토큰이 유효하지 않은 경우 401 에러 응답
                    ObjectMapper objectMapper = new ObjectMapper();
                    response.getWriter().write(objectMapper.writeValueAsString(createResponse(response, ExceptionCode.INVALID_TOKEN)));
                    return; // 필터 체인을 더 이상 진행하지 않고 즉시 응답
                }
            } else {
                // 토큰이 블랙리스트 -> 401 에러 응답
                ObjectMapper objectMapper = new ObjectMapper();
                response.getWriter().write(objectMapper.writeValueAsString(createResponse(response, ExceptionCode.UNAUTHORIZED)));
                return; // 필터 체인을 더 이상 진행하지 않고 즉시 응답
            }
        }

        filterChain.doFilter(request, response); // 다음 필터로 넘기기
    }

    private ApiResponseModel createResponse(HttpServletResponse response, ExceptionCode exceptionCode) {
        response.setStatus(exceptionCode.getStatusCode().value());
        response.setContentType("application/json");
        return new ApiResponseModel(exceptionCode.getResultCode(), exceptionCode.getResultMessage());
    }

//    @Override
//    /**
//     * JWT 토큰 검증 필터 수행
//     */
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        try{
//            String jwtHeader = request.getHeader("Authorization");
//
//            if (jwtHeader == null || !jwtHeader.startsWith("Bearer")) {
//                throw new CustomException(ExceptionCode.NOT_AUTHORIZATION);
//            }
//
//            String jwtToken = request.getHeader("Authorization").replace("Bearer ", "");
//
//            // blackList 명단에 있으면 에러 handle
//            if (accessTokenBlackListTemplate.hasKey(jwtToken)) {
//                throw new CustomException(ExceptionCode.UNAUTHORIZED);
//            }
//
//            // 요청으로 받은 accessToken 유효한지 확인
//            if (!jwtUtils.validateToken(jwtToken)) {
//                throw new CustomException(ExceptionCode.INVALID_TOKEN);
//            }
//
//            // 토큰에 저장된 유저 id 가져오기
//            String userId = jwtUtils.getUserIdFromToken(jwtToken);
//
//            //유저와 토큰 일치 시 userDetails 생성
//            UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId);
//
//            if (userDetails != null) {
//                //UserDetsils, Password, Role -> 접근권한 인증 Token 생성
//                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
//                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//
//                //현재 Request의 Security Context에 접근권한 설정
//                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
//            }
//
//            filterChain.doFilter(request, response); // 다음 필터로 넘기기
//        } catch (CustomException e) {
//            // 예외 처리: 응답에 오류 정보 작성
//            setErrorResponse(response, e);
//        }

//        filterChain.doFilter(request, response); // 다음 필터로 넘기기
//    }

//    private void setErrorResponse(
//            HttpServletResponse response,
//            CustomException ex
//    ){
//        ObjectMapper objectMapper = new ObjectMapper();
//        ExceptionCode exceptionCode = ex.getExceptionCode();
//
//        ApiResponseModel apiResponse = new ApiResponseModel(exceptionCode.getResultCode(), exceptionCode.getResultMessage());
//        apiResponse.putError(ex.getExceptionData());
//
//        response.setStatus(exceptionCode.getStatusCode().value());
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        try {
//            response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Data
//    public static class ErrorResponse{
//        private final Integer code;
//        private final String message;
//    }
}
