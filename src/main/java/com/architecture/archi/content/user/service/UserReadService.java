package com.architecture.archi.content.user.service;

import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.error.ExceptionCode;
import com.architecture.archi.config.security.user.CustomUserDetails;
import com.architecture.archi.content.user.model.UserModel;
import com.architecture.archi.db.entity.user.UserFileEntity;
import com.architecture.archi.db.repository.user.UserDao;
import com.architecture.archi.db.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserReadService {
    private final UserRepository userRepository;
    private final UserDao userDao;

    //@Transactional 기본적으로 error에 대해서만 롤백을 진행하고, Exception은 따로 옵션으로 설정해야한다.
    @Transactional(rollbackFor = Exception.class)
    public Boolean existCheckId(String id) throws CustomException {
        Boolean isExistId;
        try {
            isExistId = userRepository.existsById(id);
        }catch (Exception e){
            throw new CustomException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }

        return isExistId;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean existCheckNickName(String nickName) throws CustomException {
        Boolean isExistNickName;

        try {
            isExistNickName = userRepository.existsByNickName(nickName);
        }catch (Exception e){
            throw new CustomException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }

        return isExistNickName;
    }

    @Transactional(rollbackFor = Exception.class)
    public UserModel.UserSimpleInfoRes findUserSimpleInfo(CustomUserDetails userDetails) throws CustomException {
        Optional<UserFileEntity> optionalUserFileEntity = userDao.findUserFileWithFileByUserId(userDetails.getUsername());

        return UserModel.UserSimpleInfoRes.builder()
                .nickName(userDetails.getUser().getNickName())
                .profileImagePath(optionalUserFileEntity.isPresent() ? optionalUserFileEntity.get().getFile().getUrl() : null)
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public UserModel.UserDetailInfoRes findUserDetailInfo(CustomUserDetails userDetails) throws CustomException {
        Optional<UserFileEntity> optionalUserFileEntity = userDao.findUserFileWithFileByUserId(userDetails.getUsername());

        return UserModel.UserDetailInfoRes.builder()
                .id(userDetails.getUser().getId())
                .email(userDetails.getUser().getEmail())
                .nickName(userDetails.getUser().getNickName())
                .profileImagePath(optionalUserFileEntity.isPresent() ? optionalUserFileEntity.get().getFile().getUrl() : null)
                .build();
    }
}
