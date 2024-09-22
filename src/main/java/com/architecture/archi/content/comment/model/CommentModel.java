package com.architecture.archi.content.comment.model;

import com.architecture.archi.common.enumobj.BooleanFlag;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentModel {
    /*
    * request
    * */
    @Getter
    public static class AddCommentReq{
        @NotNull(message = "필수값입니다.")
        private Long contentId;
        private Long parentId; // 0은 제일 상위 댓글
        @NotBlank(message = "필수값입니다.")
        private String comment;
        private String sendUserNickName;
    }

    @Getter
    public static class UpdateCommentReq{
        @NotNull(message = "필수값입니다.")
        private Long commentId;
        @NotBlank(message = "필수값입니다.")
        private String comment;
    }

    @Getter
    public static class UpdateCommentLikeReq{
        @NotNull(message = "필수값입니다.")
        private Long commentId;
    }

    @Getter
    public static class DeleteCommentReq{
        private List<Long> ids;
    }


    /*
    * response
    * */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class CommentDto{
        private Long id;
        private Long parentCommentId;
        private String comment;
        private BooleanFlag delYn;
        private String userNickName;
        private String sendUserNickName;
        private String commentAuthorImgUrl;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime createdAt;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime updatedAt;
        private Long like;
        private List<String> likeUserIds;
        private Boolean isContentAuthor; // 컨텐츠 작성자 댓글인지
        private Boolean isWriteUser; // 현재 접속한 유저가 수정할 수 있는 댓글인지
        private List<CommentDto> children;

        @Builder
        public CommentDto(Long id, String comment, BooleanFlag delYn, String userNickName, String commentAuthorImgUrl, LocalDateTime createdAt, LocalDateTime updatedAt, Long like, List<String> likeUserIds, Boolean isContentAuthor, Boolean isWriteUser, List<CommentDto> children) {
            this.id = id;
            this.comment = comment;
            this.delYn = delYn;
            this.userNickName = userNickName;
            this.commentAuthorImgUrl = commentAuthorImgUrl;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.like = like;
            this.likeUserIds = likeUserIds;
            this.isContentAuthor = isContentAuthor;
            this.isWriteUser = isWriteUser;
            this.children = children;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UserCommentDto{
        private Long id;
        private Long contentId;
        private String contentTitle;
        private String comment;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime createdAt;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime updatedAt;
        private Long like;

        @Builder
        public UserCommentDto(Long id, Long contentId, String contentTitle, String comment, LocalDateTime createdAt, LocalDateTime updatedAt, Long like) {
            this.id = id;
            this.contentId = contentId;
            this.contentTitle = contentTitle;
            this.comment = comment;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.like = like;
        }
    }
}
