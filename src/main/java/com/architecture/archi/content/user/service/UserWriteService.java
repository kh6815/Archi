package com.architecture.archi.content.user.service;

import com.architecture.archi.common.EncryptUtil;
import com.architecture.archi.common.email.CommonEmail;
import com.architecture.archi.common.email.model.EmailMessage;
import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.error.ExceptionCode;
import com.architecture.archi.config.security.user.CustomUserDetails;
import com.architecture.archi.content.user.model.UserModel;
import com.architecture.archi.db.entity.user.UserEntity;
import com.architecture.archi.db.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserWriteService {

    private final UserRepository userRepository;
    private final CommonEmail commonEmail;

    //@Transactional 기본적으로 error에 대해서만 롤백을 진행하고, Exception은 따로 옵션으로 설정해야한다.
    @Transactional(rollbackFor = Exception.class)
    public String createUser(UserModel.UserSignUpReq userSignUpReq) throws CustomException {
        // ID 중복 확인
        if(userRepository.existsById(userSignUpReq.getId())){
            throw new CustomException(ExceptionCode.ALREADY_EXIST, "이미 존재하는 아이디 입니다.");
        }

        // pw, pwCheck 비교
        if(!userSignUpReq.getPw().equals(userSignUpReq.getPwCheck())){
            throw new CustomException(ExceptionCode.BAD_REQUEST, "두 패스워드가 다릅니다.");
        }

        // nickName 중복확인
        if(userRepository.existsByNickName(userSignUpReq.getNickName())){
            throw new CustomException(ExceptionCode.ALREADY_EXIST, "이미 존재하는 닉네임 입니다.");
        }

        UserEntity userEntity = UserEntity.builder()
                .id(userSignUpReq.getId())
                .pw(EncryptUtil.encryptString(userSignUpReq.getPw()))
                .email(userSignUpReq.getEmail())
                .nickName(userSignUpReq.getNickName())
                .build();

        UserEntity user = userRepository.save(userEntity);
        return user.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public UserModel.InitPasswordRes initPassword(String id) throws CustomException {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_EXIST, "존재하지 않는 아이디 입니다."));

        String randomPassword = generateRandomPassword();
        EmailMessage emailMessage = EmailMessage.builder()
                .to(user.getEmail())
                .subject("archi 커뮤니티 비밀번호 초기화 이메일 입니다.")
                .message(randomPassword + " 해당 비밀번호로 초기화 됐습니다. 다시 로그인하여 비밀번호를 변경하세요")
                .build();

        if(commonEmail.sendMail(emailMessage)){
            user.changePassword(EncryptUtil.encryptString(randomPassword));

            return UserModel.InitPasswordRes.builder()
                    .message(commonEmail.maskEmail(user.getEmail()) + "로 초기화된 이메일을 전송하였습니다.")
                    .isSuccess(true)
                    .build();
        }

        return UserModel.InitPasswordRes.builder()
                .message("")
                .isSuccess(false)
                .build();
    }

    public static String generateRandomPassword() {
        String DIGITS = "0123456789";
        String ALPHABETS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String SPECIAL_CHARACTERS = "!@#$%^&*()-_=+[]{}|;:',.<>?/~`";

        SecureRandom random = new SecureRandom();

        // 각 필수 조건을 만족하는 문자 하나씩 선택
        StringBuilder sb = new StringBuilder();
        sb.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        sb.append(ALPHABETS.charAt(random.nextInt(ALPHABETS.length())));
        sb.append(SPECIAL_CHARACTERS.charAt(random.nextInt(SPECIAL_CHARACTERS.length())));

        // 나머지 길이를 채우기 위해 랜덤한 문자들을 추가
        int remainingLength = 8 + random.nextInt(13) - 3; // 전체 길이는 8~20 사이
        String allCharacters = DIGITS + ALPHABETS + SPECIAL_CHARACTERS;

        for (int i = 0; i < remainingLength; i++) {
            sb.append(allCharacters.charAt(random.nextInt(allCharacters.length())));
        }

        // 랜덤하게 섞기
        List<Character> characters = new ArrayList<>();
        for (char c : sb.toString().toCharArray()) {
            characters.add(c);
        }
        Collections.shuffle(characters, random);

        // 결과 문자열 생성
        StringBuilder result = new StringBuilder();
        for (char c : characters) {
            result.append(c);
        }

        return result.toString();
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean changePassword(UserModel.ChangePasswordReq changePasswordReq, CustomUserDetails userDetails) throws CustomException {
        // userDetails에서 가져온 userEntity는 영속성이 없기 때문에 다시 user를 조회해 온다.
        UserEntity user = userRepository.findById(userDetails.getUser().getId())
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_EXIST, "존재하지 않는 유저"));

        if(!user.getPw().equals(EncryptUtil.encryptString(changePasswordReq.getBeforePassword()))){
            throw new CustomException(ExceptionCode.BAD_PW_REQUEST, "현재 비밀번호가 일치하지 않습니다.");
        }

        if(!changePasswordReq.getNewPassword().equals(changePasswordReq.getCheckPassword())){
            throw new CustomException(ExceptionCode.BAD_REQUEST, "두 비밀번호가 일치하지 않습니다.");
        }

        user.changePassword(EncryptUtil.encryptString(changePasswordReq.getNewPassword()));

        return true;
    }
}
