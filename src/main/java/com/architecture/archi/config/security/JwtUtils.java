package com.architecture.archi.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class JwtUtils {

    // HMAC512의 시크릿키
    @Value("${jwt.secret}")
    private String secretKey;

    // accessToken 유효시간
    @Value("${jwt.access}")
    private int accessExpirationInSec;

    // refreshToken 유효시간
    @Value("${jwt.refresh}")
    private int refreshExpirationDateInSec;

    @Autowired
    @Qualifier("accessTokenBlackListTemplate")
    private RedisTemplate<String, Object> accessTokenBlackListTemplate;

    @Autowired
    @Qualifier("refreshTokenRedisTemplate")
    private RedisTemplate<String, Object> refreshTokenRedisTemplate;

    // accessToken 생성 메서드
    public String createAccessToken(String userId) {
        String token = JWT.create()
                .withSubject("ILuvIt_AccessToken")
                .withExpiresAt(new Date(System.currentTimeMillis() + (accessExpirationInSec * 1000L)))
                .withClaim("id", userId)
                //.withIssuedAt(new Date())
                .sign(Algorithm.HMAC512(secretKey));

        log.info("token info => {}", Algorithm.HMAC512(secretKey));
        log.info("token info => {}", Algorithm.HMAC512(secretKey));

        log.info("token info => {}", token);
        return token;
    }
    // refreshToken 생성 메서드
    public String createRefreshToken(String userId) {
        String token = JWT.create()
                .withSubject("ILuvIt_RefreshToken")
                .withExpiresAt(new Date(System.currentTimeMillis() + (refreshExpirationDateInSec * 1000L)))
                .withClaim("id", userId)
                .sign(Algorithm.HMAC512(secretKey));
        return token;
    }
    // 토큰에서 사용자 id 추출
    public String getUserIdFromToken(String token) {
        DecodedJWT jwt = JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
        return jwt.getClaim("id").asString();
    }
    // 토큰이 만료됐는지 check
    public Boolean isExpired(String token) {
        try {
            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
            return false;
        } catch (TokenExpiredException e) {
            log.warn("[JwtVerificationException] token 기간 만료 : {}", e.getMessage());
            return true;
        } catch (JWTVerificationException e) {
            log.warn("[JWTVerificationException] token 파싱 실패 : {}", e.getMessage());
            return false;
        }
    }
    // 토큰이 유효한지 check
    public Boolean validateToken(String token) throws JWTVerificationException{
        try {
            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
            return true;
        } catch (JWTVerificationException e) {
            log.warn("[JWTVerificationException] token 파싱 실패 : {}", e.getMessage());
            return false;
        }
    }
    // 토큰 타입 명시
    public String addPrefix(String token) {
        return "Bearer " + token;
    }

    // logout시 현재 사용중인 accessToken blacklist에 등록
    public void setAccessTokenToBlackList(String userid, String accessToken) {
        if (StringUtils.hasText(userid) && StringUtils.hasText(accessToken)){
            accessTokenBlackListTemplate.opsForValue().set(accessToken, userid, accessExpirationInSec * 1000L, TimeUnit.MILLISECONDS);
        }
    }

    // redis에 refreshToken 토큰 저장
    public void setRedisAuthToken(String userId, String refreshToken) {
//        authRedisTemplate.opsForValue().set(userId, adminUser, authTokenExpire, TimeUnit.DAYS);
        if (StringUtils.hasText(userId) && StringUtils.hasText(refreshToken)){
            refreshTokenRedisTemplate.opsForValue().set(refreshToken, userId, refreshExpirationDateInSec * 1000L, TimeUnit.MILLISECONDS);
        }
    }

    // redis에 저장된 refreshToken 토큰 삭제
    public void destroyRedisAuthToken(String refreshAuthToken) {
//        if (!StringUtils.isEmpty(userId))
//            authRedisTemplate.delete(userId);
        if (StringUtils.hasText(refreshAuthToken) && refreshTokenRedisTemplate.hasKey(refreshAuthToken)){
            refreshTokenRedisTemplate.delete(refreshAuthToken);
        }
    }
}
