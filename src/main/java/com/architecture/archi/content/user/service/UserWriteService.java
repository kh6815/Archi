package com.architecture.archi.content.user.service;

import com.architecture.archi.common.EncryptUtil;
import com.architecture.archi.common.email.CommonEmail;
import com.architecture.archi.common.email.model.EmailMessage;
import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.error.ExceptionCode;
import com.architecture.archi.config.security.user.CustomUserDetails;
import com.architecture.archi.content.user.model.UserModel;
import com.architecture.archi.db.entity.auth.TokenPairEntity;
import com.architecture.archi.db.entity.file.FileEntity;
import com.architecture.archi.db.entity.user.UserEntity;
import com.architecture.archi.db.entity.user.UserFileEntity;
import com.architecture.archi.db.repository.auth.TokenPairRepository;
import com.architecture.archi.db.repository.file.FileRepository;
import com.architecture.archi.db.repository.user.UserDao;
import com.architecture.archi.db.repository.user.UserFileRepository;
import com.architecture.archi.db.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserWriteService {

    private final UserRepository userRepository;
    private final CommonEmail commonEmail;
    private final UserFileRepository userFileRepository;
    private final FileRepository fileRepository;
    private final UserDao userDao;
    private final TokenPairRepository tokenPairRepository;

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

        String encryptRandomPassword = EncryptUtil.encryptString(randomPassword);

        commonEmail.sendMail(emailMessage).addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(Boolean result) {
                // 비동기 동작은 메인스레드가 아닌 분리된 스레드에서 동작함으로 메인 스레드의 트랜잭션 범위를 벗어남
                // 고로 따로 트랜잭션을 동작하는 메소드를 형태로 구현

                // 비동기 성공 후에 트랜잭션을 재시작하여 비밀번호 저장
                saveUserPassword(user, encryptRandomPassword);
            }

            @Override
            public void onFailure(Throwable ex) {
                // 이메일 전송 실패 시 처리
                log.error("이메일 전송 실패: " + ex.getMessage());
            }
        });
        return UserModel.InitPasswordRes.builder()
                .message(commonEmail.maskEmail(user.getEmail()) + "로 초기화된 이메일을 전송하였습니다. (이메일 전송시 몇분의 시간이 걸릴 수 있습니다.)")
                .isSuccess(true)
                .build();
    }

    // 별도의 트랜잭션을 사용하여 비밀번호 저장
    @Transactional
    protected void saveUserPassword(UserEntity user, String newPassword) {
        user.changePassword(newPassword);
        userRepository.save(user);
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

    @Transactional(rollbackFor = Exception.class)
    public Boolean changeNickName(UserModel.ChangeNickNameReq changeNickNameReq, CustomUserDetails userDetails) throws CustomException {

        if(userRepository.existsByNickName(changeNickNameReq.getNewNickName())){
            throw new CustomException(ExceptionCode.ALREADY_EXIST, "이미 존재하는 닉네임 입니다.");
        }

        // userDetails에서 가져온 userEntity는 영속성이 없기 때문에 다시 조회
        UserEntity user = userRepository.findById(userDetails.getUser().getId())
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_EXIST, "존재하지 않는 유저"));

        user.changeNickName(changeNickNameReq.getNewNickName());

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean changeImage(UserModel.ChangeImageReq changeImageReq, CustomUserDetails userDetails) throws CustomException {
        // 기존에 있던 UserFile은 삭제
        Optional<UserFileEntity> existUserFileEntity = userDao.findUserFileByUserId(userDetails.getUsername());
        existUserFileEntity.ifPresent(UserFileEntity::delete);

        FileEntity fileEntity = fileRepository.findById(changeImageReq.getFileId())
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_EXIST, "존재하지 않은 파일"));
        UserFileEntity userFileEntity = UserFileEntity.builder()
                .user(userDetails.getUser())
                .file(fileEntity)
                .build();

        userFileRepository.save(userFileEntity);

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteUser(CustomUserDetails userDetails) throws CustomException {
        // 유저 delYn 변경
        UserEntity userEntity = userRepository.findById(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_EXIST, "존재하지 않는 유저"));

        userEntity.deleteUser();

        // token_pair 테이블 삭제
        Optional<TokenPairEntity> optionalTokenPairEntity = tokenPairRepository.findByUserId(userDetails.getUsername());
        optionalTokenPairEntity.ifPresent(tokenPairRepository::delete);

        // user_file delYn 변경
        Optional<UserFileEntity> optionalUserFileEntity = userDao.findUserFileByUserId(userDetails.getUsername());
        optionalUserFileEntity.ifPresent(userFileRepository::delete);

        // TODO 관련 content, content_file, comment 같은 경우 그 수가 너무 많을 수 있으므로 batch 나중에 삭제 ->  batch 돌릴때 1000 단위로 끊어서 돌리기

        return true;
    }
}
