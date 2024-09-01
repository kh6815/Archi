package com.architecture.archi.content.comment.service;

import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.config.security.user.CustomUserDetails;
import com.architecture.archi.content.comment.model.CommentModel;
import com.architecture.archi.db.entity.comment.CommentEntity;
import com.architecture.archi.db.repository.comment.CommentDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        return commentDao.findCommentsByContent(contentId, userDetails.getUsername());
    }
}
