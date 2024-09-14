package com.architecture.archi.config.security;

import com.architecture.archi.config.security.error.CustomAccessDeniedHandler;
import com.architecture.archi.config.security.error.CustomAuthenticationEntryPoint;
import com.architecture.archi.config.security.filter.JwtAuthFilter;
import com.architecture.archi.config.security.user.CustomUserDetailsService;
import com.architecture.archi.config.security.user.oauth2.CustomOauth2UserService;
import com.architecture.archi.config.security.user.oauth2.OAuth2FailureHandler;
import com.architecture.archi.config.security.user.oauth2.OAuth2SuccessHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/*
 * (JWT 인가에 대한)스프링 시큐리티 필터 vs 인터셉터
 * 스프링 시큐리티 필터
 *      장점
 *          - 필터로 할 경우에는 서블릿 앞단에서 동작함으로 인터셉터보다 빠르게 처리가 가능함
 *          - 필터를 이용한 보안성 강화
 *      단점
 *          - permitAll()를 적용해도 필터는 무조건 동작함으로, 예외처리에 어려움.
 *          - @controllerAdvice에서 캐치하는 영역 밖이므로 따로 에러처리를 해야함.
 *          - 필터 로직에서 에러를 발생시켜 동작을 멈추고 싶지만 그럴 경우 페이지 이동이 안됨(filterChain.doFilter를 동작시켜야 함)
 *              ex) permitAll()를 적용해도 필터는 무조건 동작함으로 /login이나, /swagger에 접근시 -> JwtAuthFilter에서 헤더에 토큰이 없으면 에러를 발생시켜서 페이지 이동이 안됨)
 *
 * 인터셉터
 *      장점
 *          - @controllerAdvice에서 공통된 예외처리가 가능
 *          - 메서드별 인가애노테이션을 달아서 가독성에 유리함
 *      단점
 *          - 필터에 비해 속도가 느리다는 단점
 * */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@AllArgsConstructor
public class SecurityConfig  {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtils jwtUtil;
    private final RedisTemplate<String, Object> accessTokenBlackListTemplate;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final CustomOauth2UserService customOauth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //CSRF, CORS
        http.csrf((csrf) -> csrf.disable()); //CSRF 보호 비활성화 : CSRF 토큰을 사용하지 않을 것이므로 확인하지 않도록 설정
//        http.cors(Customizer.withDefaults()); //CORS 설정을 적용 : 다른 도메인의 웹 페이지에서 리소스에 접근할 수 있도록 허용
        http.cors(cors -> cors.configurationSource(corsConfigurationSource())); // CORS 설정 적용


        //세션 관리 상태 없음으로 구성, Spring Security가 세션 생성 or 사용 X
        http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS));

        //FormLogin, BasicHttp 비활성화
        http.formLogin((form) -> form.disable());
        http.httpBasic(AbstractHttpConfigurer::disable);

//        // OAuth 2.0 로그인 방식 설정
//        http.oauth2Login((auth) -> auth.loginPage("/oauth-login/login")
//                .successHandler(oAuth2SuccessHandler)
////                        .defaultSuccessUrl("/oauth-login") //기본 리디렉션 URL 설정: 사용자가 OAuth2 인증을 성공적으로 마친 후 이동할 기본 페이지를 지정합니다.
//                .failureUrl("/oauth-login/login")
//                .permitAll());

        // oauth2 설정
        http.oauth2Login(oauth -> // OAuth2 로그인 기능에 대한 여러 설정의 진입점
                // OAuth2 로그인 성공 이후 사용자 정보를 가져올 때의 설정을 담당
                oauth.userInfoEndpoint(c -> c.userService(customOauth2UserService))
                        // 로그인 성공 시 핸들러
                        .successHandler(oAuth2SuccessHandler)
                        // 로그인 실패 시 핸들러
                        .failureHandler(oAuth2FailureHandler)
        );

        //JwtAuthFilter를 UsernamePasswordAuthenticationFilter 앞에 추가
        http.addFilterBefore(new JwtAuthFilter(customUserDetailsService, jwtUtil, accessTokenBlackListTemplate), UsernamePasswordAuthenticationFilter.class);

        // 인증 / 인가시 에러 처리
        http.exceptionHandling((exceptionHandling) -> exceptionHandling
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
        );

        // 권한 규칙 작성
        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/swagger-ui.html")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/v3/api-docs/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api-docs")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/v1/user/signup")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/v1/user/check-id")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/v1/user/check-email")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/v1/user/check-nickname")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/v1/user/init-password")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/v1/auth/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/v1/content/list/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/v1/comment/list/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/index.html")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/login/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/login")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/error")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/favicon.ico")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/login/oauth2/code/**")).permitAll()
                        //@PreAuthrization을 사용할 것이기 때문에 모든 경로에 대한 인증처리는 Pass
//                        .anyRequest().permitAll()
                        .anyRequest().authenticated()
        );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // 허용할 출처
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS")); // 허용할 HTTP 메서드
        configuration.setAllowedHeaders(Arrays.asList("*")); // 허용할 헤더
        configuration.setAllowCredentials(true); // 자격 증명 포함 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}