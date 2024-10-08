package com.architecture.archi.content.comment.service;

import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.config.security.user.CustomUserDetails;
import com.architecture.archi.content.comment.model.CommentModel;
import com.architecture.archi.db.entity.comment.CommentEntity;
import com.architecture.archi.db.repository.comment.CommentDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentReadService {

    private final CommentDao commentDao;

    @Transactional(rollbackFor = Exception.class)
    public List<CommentModel.CommentDto> findComments(Long contentId, CustomUserDetails userDetails) throws CustomException {
        String userId = null;
        if(userDetails != null){
            userId = userDetails.getUsername();
        }
        return commentDao.findCommentsByContent(contentId, userId);
    }

    @Transactional(rollbackFor = Exception.class)
    public Page<CommentModel.UserCommentDto> findUserComments(CustomUserDetails userDetails, Pageable pageable) throws CustomException {
        return commentDao.findUserCommentsPagingByUserId(userDetails.getUsername(), pageable);
    }
}
