package com.architecture.archi.config.security.user.oauth2;

import com.architecture.archi.common.enumobj.BooleanFlag;
import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.error.ExceptionCode;
import com.architecture.archi.config.security.user.oauth2.google.GoogleUserDetails;
import com.architecture.archi.db.entity.user.UserEntity;
import com.architecture.archi.db.repository.user.UserDao;
import com.architecture.archi.db.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final UserDao userDao;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo oAuth2UserInfo = null;

        // 뒤에 진행할 다른 소셜 서비스 로그인을 위해 구분 => 구글
        if(provider.equals("google")){
            oAuth2UserInfo = new GoogleUserDetails(oAuth2User.getAttributes());
        }

        String providerId = oAuth2UserInfo.getProviderId();
        String email = oAuth2UserInfo.getEmail();
        String loginId = provider + "_" + providerId;
        String name = oAuth2UserInfo.getName();

        Optional<UserEntity> findUser = userRepository.findById(loginId);
        UserEntity userEntity;

        if (findUser.isPresent()) {
            userEntity = findUser.get();

            // 이미 삭제된 유저 일 경우
            if(userEntity.getDelYn().equals(BooleanFlag.Y)){
                // 다시 유저 활성화
                userEntity.comeBackUser();
            }
        } else{
            // TODO 이미 존재하는 이메일일 경우 에러 발생
            boolean isExistEmail = userRepository.existsByEmail(email);

            if (isExistEmail) {
                throw new OAuth2AuthenticationException(ExceptionCode.ALREADY_EXIST.getResultMessage());
            }

            String now = LocalDateTime.now().toString();
            for (String s : Arrays.asList("-", ".", ":", "T")) {
                now = now.replace(s,"");
            }

            userEntity = UserEntity.builder()
                    .id(loginId)
                    .email(email)
                    .nickName(name + now)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(userEntity);
        }

        return new CustomOauth2UserDetails(userEntity, oAuth2User.getAttributes());
    }
}