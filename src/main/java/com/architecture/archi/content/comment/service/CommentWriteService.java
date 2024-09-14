package com.architecture.archi.content.comment.service;

import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.error.ExceptionCode;
import com.architecture.archi.config.security.user.CustomUserDetails;
import com.architecture.archi.content.comment.model.CommentModel;
import com.architecture.archi.content.content.model.ContentModel;
import com.architecture.archi.db.entity.comment.CommentEntity;
import com.architecture.archi.db.entity.content.ContentEntity;
import com.architecture.archi.db.entity.like.CommentLikeEntity;
import com.architecture.archi.db.entity.like.ContentLikeEntity;
import com.architecture.archi.db.entity.user.UserEntity;
import com.architecture.archi.db.repository.comment.CommentDao;
import com.architecture.archi.db.repository.comment.CommentRepository;
import com.architecture.archi.db.repository.content.ContentDao;
import com.architecture.archi.db.repository.content.ContentRepository;
import com.architecture.archi.db.repository.like.CommentLikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentWriteService {

    private final CommentRepository commentRepository;
    private final ContentDao contentDao;
    private final CommentDao commentDao;

    private final CommentLikeRepository commentLikeRepository;

    @Transactional(rollbackFor = Exception.class)
    public Long createComment(CommentModel.AddCommentReq addCommentReq, CustomUserDetails userDetails) throws CustomException {
        CommentEntity parentCommentEntity = null;
        if(addCommentReq.getParentId() != 0){
            parentCommentEntity = commentRepository.findById(addCommentReq.getParentId())
                    .orElseThrow(() -> new CustomException(ExceptionCode.NOT_EXIST, "존재하지 않는 댓글"));
        }

        UserEntity userEntity = userDetails.getUser();
        ContentEntity contentEntity = contentDao.findContentWithUser(addCommentReq.getContentId());

        CommentEntity commentEntity = CommentEntity.builder()
                .parent(parentCommentEntity)
                .user(userEntity)
                .content(contentEntity)
                .comment(addCommentReq.getComment())
                .isContentAuthor(contentEntity.getUser().getId().equals(userDetails.getUsername()))
                .build();

        CommentEntity comment = commentRepository.save(commentEntity);
        return comment.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean updateComment(CommentModel.UpdateCommentReq updateCommentReq, CustomUserDetails userDetails) throws CustomException {
        CommentEntity commentEntity = commentDao.findComment(updateCommentReq.getCommentId());

        if(!userDetails.getUsername().equals(commentEntity.getUser().getId())){
            throw new CustomException(ExceptionCode.INVALID, "댓글 작성자가 아니면 수정할 수 없습니다.");
        }

//        commentDao.updateComment(updateCommentReq.getCommentId(), updateCommentReq.getComment());
        commentEntity.updateComment(updateCommentReq.getComment());
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteComment(Long id, CustomUserDetails userDetails) throws CustomException {
        CommentEntity commentEntity = commentDao.findComment(id);

        if(!userDetails.getUsername().equals(commentEntity.getUser().getId())){
            throw new CustomException(ExceptionCode.INVALID, "댓글 작성자가 아니면 삭제할 수 없습니다.");
        }

        commentEntity.deleteComment();
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean updateLike(CommentModel.UpdateCommentLikeReq updateCommentLikeReq, CustomUserDetails userDetails) throws CustomException {
        Optional<CommentLikeEntity> likeEntityOptional = commentLikeRepository.findByUser_IdAndComment_Id(userDetails.getUsername(), updateCommentLikeReq.getCommentId());

        if(likeEntityOptional.isPresent()){
            commentLikeRepository.delete(likeEntityOptional.get());
        } else {
            CommentEntity commentEntity = commentDao.findSimpleComment(updateCommentLikeReq.getCommentId());

            CommentLikeEntity commentLikeEntity = CommentLikeEntity.builder()
                    .user(userDetails.getUser())
                    .comment(commentEntity)
                    .build();

            commentLikeRepository.save(commentLikeEntity);
        }

        return true;
    }
}
