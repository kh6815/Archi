package com.architecture.archi.content.user.service;

import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.error.ExceptionCode;
import com.architecture.archi.db.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserReadService {
    private final UserRepository userRepository;

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
}
