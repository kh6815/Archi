package com.architecture.archi.content.auth.service;

import com.architecture.archi.common.EncryptUtil;
import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.error.ExceptionCode;
import com.architecture.archi.common.model.ApiResponseModel;
import com.architecture.archi.config.security.JwtUtils;
import com.architecture.archi.content.auth.model.AuthModel;
import com.architecture.archi.db.entity.auth.TokenPairEntity;
import com.architecture.archi.db.entity.user.UserEntity;
import com.architecture.archi.db.repository.auth.TokenPairRepository;
import com.architecture.archi.db.repository.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtils jwtUtils;
    private final TokenPairRepository tokenPairRepository;
    private final UserRepository userRepository;

    @Qualifier("refreshTokenRedisTemplate")
    private final RedisTemplate<String, Object> refreshTokenRedisTemplate;

    @Transactional(rollbackFor = Exception.class)
    public ApiResponseModel<AuthModel.AuthLoginRes> login(AuthModel.AuthLoginReq request) throws CustomException {

        UserEntity user = userRepository.findById(request.getId())
                .orElseThrow(() -> new CustomException(ExceptionCode.BAD_ID_REQUEST));

        if(!user.getPw().equals(EncryptUtil.encryptString(request.getPw()))){
            throw new CustomException(ExceptionCode.BAD_PW_REQUEST);
        }

        return makeToken(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiResponseModel<AuthModel.AuthLoginRes> oauthLogin(String oauthId) throws CustomException {

        UserEntity user = userRepository.findById(oauthId)
                .orElseThrow(() -> new CustomException(ExceptionCode.BAD_ID_REQUEST));

        return makeToken(user);
    }

    private ApiResponseModel<AuthModel.AuthLoginRes> makeToken(UserEntity user) {

        // 인증 객체를 통해 tokenPair 생성
        String jwt = jwtUtils.createAccessToken(user.getId());
        String refresh = jwtUtils.createRefreshToken(user.getId());
        TokenPairEntity tokenPair = TokenPairEntity.createTokenPair(jwt, refresh, user);

        // 기존 토큰이 있으면 수정, 없으면 생성
        tokenPairRepository.findByUserId(user.getId())
                .ifPresentOrElse(
                        (findTokenPair) -> {
                            // logout 하고 login 경우
                            if(findTokenPair.getAccessToken() == null && findTokenPair.getRefreshToken() == null){
                                findTokenPair.updateToken(jwt, refresh);
                            } else{ // logout 안하고 재로그인 할 경우
                                // 기존 access 토큰 블랙리스트
                                jwtUtils.setAccessTokenToBlackList(user.getId(), findTokenPair.getAccessToken());
                                // 기존에 redis에 있는 refreshToken 삭제
                                jwtUtils.destroyRedisAuthToken(findTokenPair.getRefreshToken());
                                // 토큰 쌍 Db에 업데이트
                                findTokenPair.updateToken(jwt, refresh);
                            }
                        },
                        // 새로운 유저일 경우
                        () -> tokenPairRepository.save(tokenPair)
                );

        jwtUtils.setRedisAuthToken(user.getId(), refresh);

        AuthModel.AuthLoginRes result = new AuthModel.AuthLoginRes();
        result.setId(user.getId());
        result.setAccessToken(jwtUtils.addPrefix(jwt));
        result.setRefreshToken(jwtUtils.addPrefix(refresh));
        result.setRole(user.getRole());

        return new ApiResponseModel<>(result);
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiResponseModel<AuthModel.AuthLoginRes> logout(String userId) throws CustomException {

        //accessToken을 가지고 유저 체크 처리 해야됨

//        //아이디에 해당하는 유저가 없을 경우 에러처리
//        userRepository.findById(request.getId())
//                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_EXIST));

        // 기존 토큰이 있으면 삭제, 없으면 에러
        tokenPairRepository.findByUserId(userId)
                .ifPresentOrElse(
                        (findTokenPair) -> {
                            initLogOut(userId, findTokenPair);
                        },
                        () -> { new CustomException(ExceptionCode.NO_TOKEN);}
                );

        AuthModel.AuthLoginRes result = new AuthModel.AuthLoginRes();
//        result.setId(userId);
        return new ApiResponseModel<>(result);
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiResponseModel<AuthModel.AuthLoginRes> refresh(HttpServletRequest request) throws CustomException {
        String jwtHeader = request.getHeader("Authorization");

        if (!StringUtils.hasText(jwtHeader) || !jwtHeader.startsWith("Bearer")) {
            throw new CustomException(ExceptionCode.NOT_AUTHORIZATION);
        }

        String requestRefreshToken = request.getHeader("Authorization").replace("Bearer ", "");

        // redis에 refreshToken이 있는지 확인
        if(!refreshTokenRedisTemplate.hasKey(requestRefreshToken)){
            // 레디스에 저장된 토큰이 없음
            throw new CustomException(ExceptionCode.NO_TOKEN);
        }

        // 요청으로 받은 refreshToken 유효한지 확인
        if(!jwtUtils.validateToken(requestRefreshToken)){
            throw new CustomException(ExceptionCode.INVALID_TOKEN);
        }

        // 이전에 받았던 refreshToken과 일치하는지 확인(tokenPair 유저당 하나로 유지)
        String userId = jwtUtils.getUserIdFromToken(requestRefreshToken);
        TokenPairEntity findTokenPair = tokenPairRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_EXIST)); //- 유저가 없음.

        if (!requestRefreshToken.equals(findTokenPair.getRefreshToken())) {
            // 유효하지 않은 토큰
            throw new CustomException(ExceptionCode.INVALID_TOKEN);
        }

        // 이전에 발급했던 AccessToken 만료되지 않았다면 refreshToken 탈취로 판단
        // TokenPair 삭제 -> 다시 로그인 해야됨
        if (jwtUtils.isExpired(findTokenPair.getAccessToken())) {
            UserEntity user = userRepository.findById(findTokenPair.getUser().getId())
                    .orElseThrow(() -> new CustomException(ExceptionCode.NOT_EXIST));

            // access 토큰은 어짜피 만료된 토큰임으로 blacklist에 넣을 필요 x
            // 기존 redis에 있는 refreshToken 삭제
            jwtUtils.destroyRedisAuthToken(findTokenPair.getRefreshToken());

            String jwt = jwtUtils.createAccessToken(user.getId());
            String refresh = jwtUtils.createRefreshToken(user.getId());
            findTokenPair.updateToken(jwt, refresh);

            // redis에 새로운 refreshToken 생성
            jwtUtils.setRedisAuthToken(userId, refresh);

//            AuthModel.AuthResponse result = principal.getUser().getUserInfo();
            AuthModel.AuthLoginRes result = new AuthModel.AuthLoginRes();
            result.setId(user.getId());
            result.setAccessToken(jwtUtils.addPrefix(jwt));
            result.setRefreshToken(jwtUtils.addPrefix(refresh));
            result.setRole(user.getRole());
            return new ApiResponseModel<>(result);

        } else {
            // accessToken이 아직 만료되지 않은 상태 -> 토큰 탈취로 판단 -> 다시 로그인 유도(다시 로그인하도록 로그아웃시킴)
            initLogOut(userId, findTokenPair);
            throw new CustomException(ExceptionCode.BAD_REQUEST, "logout");
//            return null;
        }
    }

    public void initLogOut(String userId, TokenPairEntity tokenPair){
        jwtUtils.setAccessTokenToBlackList(userId, tokenPair.getAccessToken());
        jwtUtils.destroyRedisAuthToken(tokenPair.getRefreshToken());
        tokenPair.updateToken(null, null);
    }
}
