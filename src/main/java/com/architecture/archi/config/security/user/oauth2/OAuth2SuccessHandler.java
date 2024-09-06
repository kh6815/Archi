package com.architecture.archi.config.security.user.oauth2;

import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.model.ApiResponseModel;
import com.architecture.archi.content.auth.model.AuthModel;
import com.architecture.archi.content.auth.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;
    private static final String URI = "/index.html";
//    private final CustomUserDetailsService userDetailsService;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOauth2UserDetails oAuth2User = (CustomOauth2UserDetails) authentication.getPrincipal();

        ApiResponseModel<AuthModel.AuthLoginRes> apiResponse = null;
        try {
            apiResponse = authService.oauthLogin(oAuth2User.getUsername());
        } catch (CustomException e) {
            throw new RuntimeException(e);
        }

        // 응답을 JSON 형식으로 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // ObjectMapper를 사용하여 ApiResponseModel을 JSON으로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(apiResponse);

        response.getWriter().write(jsonResponse);
//        response.sendRedirect(redirectUrl); // 나중에 클라쪽에서 access,refresh 저장 후 리다이렉트 하도록 세팅
    }
}
