package com.architecture.archi.content.notification.model;

import com.architecture.archi.common.enumobj.BooleanFlag;
import com.architecture.archi.content.comment.model.CommentModel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

public class NotificationModel {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class NotificationListDto{
        private Long id;
        private Long contendId;
        private String sendUserNickName;
        private String message;
        private BooleanFlag readYn;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime createdAt;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime updatedAt;

        @Builder
        public NotificationListDto(Long id, Long contendId, String sendUserNickName, String message, BooleanFlag readYn, LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.id = id;
            this.contendId = contendId;
            this.sendUserNickName = sendUserNickName;
            this.message = message;
            this.readYn = readYn;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }
    }
}
