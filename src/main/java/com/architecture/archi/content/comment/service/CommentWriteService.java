package com.architecture.archi.content.comment.service;

import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.error.ExceptionCode;
import com.architecture.archi.config.security.user.CustomUserDetails;
import com.architecture.archi.content.comment.model.CommentModel;
import com.architecture.archi.content.content.model.ContentModel;
import com.architecture.archi.content.notification.controller.NotificationController;
import com.architecture.archi.content.notification.service.NotificationService;
import com.architecture.archi.db.entity.comment.CommentEntity;
import com.architecture.archi.db.entity.content.ContentEntity;
import com.architecture.archi.db.entity.content.ContentFileEntity;
import com.architecture.archi.db.entity.like.CommentLikeEntity;
import com.architecture.archi.db.entity.like.ContentLikeEntity;
import com.architecture.archi.db.entity.notification.NotificationEntity;
import com.architecture.archi.db.entity.user.UserEntity;
import com.architecture.archi.db.repository.comment.CommentDao;
import com.architecture.archi.db.repository.comment.CommentRepository;
import com.architecture.archi.db.repository.content.ContentDao;
import com.architecture.archi.db.repository.content.ContentRepository;
import com.architecture.archi.db.repository.like.CommentLikeRepository;
import com.architecture.archi.db.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.architecture.archi.db.entity.comment.QCommentEntity.commentEntity;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentWriteService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ContentDao contentDao;
    private final CommentDao commentDao;

    private final CommentLikeRepository commentLikeRepository;

    private final NotificationService notificationService;

    @Transactional(rollbackFor = Exception.class)
    public Long createComment(CommentModel.AddCommentReq addCommentReq, CustomUserDetails userDetails) throws CustomException {
        CommentEntity parentCommentEntity = null;
        if(addCommentReq.getParentId() != 0){
            parentCommentEntity = commentRepository.findById(addCommentReq.getParentId())
                    .orElseThrow(() -> new CustomException(ExceptionCode.NOT_EXIST, "존재하지 않는 댓글"));
        }

        Optional<UserEntity> sendUserEntityOptional = userRepository.findByNickName(addCommentReq.getSendUserNickName());
        if(sendUserEntityOptional.isEmpty()){
            throw new CustomException(ExceptionCode.NOT_EXIST, "보내는 대상이 존재하지 않습니다.");
        }

//        if(sendUserEntityOptional.get().getId().equals(userDetails.getUsername())){
//            throw new CustomException(ExceptionCode.ALREADY_EXIST, "자기 자신한테는 답글을 쓸 수 없습니다.");
//        }

        UserEntity userEntity = userDetails.getUser();
        ContentEntity contentEntity = contentDao.findContentWithUser(addCommentReq.getContentId());

        CommentEntity commentEntity = CommentEntity.builder()
                .parent(parentCommentEntity)
                .user(userEntity)
                .sendUser(sendUserEntityOptional.get())
                .content(contentEntity)
                .comment(addCommentReq.getComment())
                .isContentAuthor(contentEntity.getUser().getId().equals(userDetails.getUsername()))
                .build();

        CommentEntity comment = commentRepository.save(commentEntity);

        // 자기 자신이면 알람을 보낼 필요 없음
        if(sendUserEntityOptional.get().getId().equals(userDetails.getUsername())){
            return comment.getId();
        }

        // 알림 전송 로직 추가
        try {
            NotificationEntity notificationEntity = NotificationEntity.builder()
                    .sender(userEntity)
                    .receiver(sendUserEntityOptional.get())
                    .content(contentEntity)
                    .message(contentEntity.getTitle() + " 해당 게시글에" +
                            " " + userEntity.getNickName() + "님이" +
                            " 새로운 답글을 달았습니다.")
                    .build();
            notificationService.sendNotification(notificationEntity);
        } catch (IOException e) {
            throw new CustomException(ExceptionCode.INTERNAL_SERVER_ERROR, "알림 전송 중 오류 발생");
        }
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
    public Boolean deleteComment(CommentModel.DeleteCommentReq deleteCommentReq, CustomUserDetails userDetails) throws CustomException {
        List<CommentEntity> commentEntityList = commentDao.findComments(deleteCommentReq.getIds());

        for (CommentEntity comment : commentEntityList) {
            if(!userDetails.getUsername().equals(comment.getUser().getId())){
                throw new CustomException(ExceptionCode.INVALID, "댓글 작성자가 아니면 삭제할 수 없습니다.");
            }

            comment.deleteComment();
        }

        commentRepository.saveAll(commentEntityList);
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
