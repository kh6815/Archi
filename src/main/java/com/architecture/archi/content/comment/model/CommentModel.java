package com.architecture.archi.content.comment.model;

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
        private Long parentId; // 0은 제일 상위 댓글
        @NotNull(message = "필수값입니다.")
        private Long contentId;
        @NotBlank(message = "필수값입니다.")
        private String comment;
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


    /*
    * response
    * */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class CommentDto{
        private Long id;
        private String comment;
        private String userNickName;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime createdAt;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime updatedAt;
        private Boolean isContentAuthor; // 컨텐츠 작성자 댓글인지
        private Boolean isWriteUser; // 현재 접속한 유저가 수정할 수 있는 댓글인지
        private Long like;
        private List<CommentDto> children;

        @Builder
        public CommentDto(Long id, String comment, String userNickName, LocalDateTime createdAt, LocalDateTime updatedAt, Boolean isContentAuthor, Boolean isWriteUser, Long like, List<CommentDto> children) {
            this.id = id;
            this.comment = comment;
            this.userNickName = userNickName;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.isContentAuthor = isContentAuthor;
            this.isWriteUser = isWriteUser;
            this.like = like;
            this.children = children;
        }
    }
}
