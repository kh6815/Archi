package com.architecture.archi.content.user.service;

import com.architecture.archi.content.user.model.UserModel;
import com.architecture.archi.db.entity.user.UserEntity;
import com.architecture.archi.db.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserWriteService {

    private final UserRepository userRepository;

    //@Transactional 기본적으로 error에 대해서만 롤백을 진행하고, Exception은 따로 옵션으로 설정해야한다.
    @Transactional(rollbackFor = Exception.class)
    public String createUser(UserModel.UserSignUpReq userSignUpReq) {
        //TODO 등록전 id, nickname 중복 확인 필요

        UserEntity userEntity = UserEntity.builder()
                .id(userSignUpReq.getId())
                .pw(userSignUpReq.getPw())
                .email(userSignUpReq.getEmail())
                .nickName(userSignUpReq.getNickName())
                .build();

        UserEntity user = userRepository.save(userEntity);
        return user.getId();
    }
}
