package com.architecture.archi.content.content.model;

import com.architecture.archi.common.enumobj.BooleanFlag;
import com.architecture.archi.content.file.model.FileModel;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ContentModel {
    /*
    * request
    * */
    @Getter
    public static class AddContentReq{
        @NotNull(message = "필수값입니다.")
        private Long categoryId;

        @NotBlank(message = "필수값입니다.")
        private String title;

        @NotBlank(message = "필수값입니다.")
        private String content;

        private List<Long> imgFileIdList;
    }

//    @Getter
//    public static class UpdateContentReq{
//        private Long id;
//        private Long categoryId;
//        private String title;
//        private String content;
//        private List<Long> imgFileIdList;
//    }

    @Getter
    @ToString
    public static class UpdateContentReq{
        private Long id;
        private Long categoryId;
        private String title;
        private String content;
        private List<Long> addFileIdList;
        private Map<Long, String> updateFileMap;
        private List<Long> deleteFileIdList;
    }

    @Getter
    public static class DeleteContentReq{
        private List<Long> ids;
    }

    @Getter
    public static class UpdateContentLikeReq{
        private Long contentId;
    }


    /*
    * response
    * */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ContentDto{
        private Long id;
        private String categoryName;
//        private BooleanFlag delYn;
        private String title;
        private String content;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime updatedAt;
        private Boolean isAvailableUpdate;
        private String contentAuthorNickName;
        private String contentAuthorImgUrl;
        private Long like;
        private List<String> likeUserIds;
        private List<FileModel.FileRes> fileList;

        @Builder
        public ContentDto(Long id, String categoryName, String title, String content, LocalDateTime updatedAt, Boolean isAvailableUpdate, String contentAuthorNickName, String contentAuthorImgUrl, Long like) {
            this.id = id;
            this.categoryName = categoryName;
            this.title = title;
            this.content = content;
            this.updatedAt = updatedAt;
            this.isAvailableUpdate = isAvailableUpdate;
            this.contentAuthorNickName = contentAuthorNickName;
            this.contentAuthorImgUrl = contentAuthorImgUrl;
            this.like = like;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ContentListDto{
        private Long id;
        private String categoryName;
        private String title;
        private String content;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime updatedAt;
        private Long like;
        private String imgUrl;

        @Builder
        public ContentListDto(Long id, String categoryName, String title, String content, LocalDateTime updatedAt, Long like) {
            this.id = id;
            this.categoryName = categoryName;
            this.title = title;
            this.content = content;
            this.updatedAt = updatedAt;
            this.like = like;
//            this.imgUrl = imgUrl;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class NoticeDto{
        private Long id;
        private String title;
        private String content;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime updatedAt;
        private Boolean isAvailableUpdate;
        private String noticeAuthorNickName;
        private String noticeAuthorImgUrl;
        private List<FileModel.FileRes> fileList;

        @Builder
        public NoticeDto(Long id, String title, String content, LocalDateTime updatedAt, Boolean isAvailableUpdate, String noticeAuthorNickName, String noticeAuthorImgUrl) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.updatedAt = updatedAt;
            this.isAvailableUpdate = isAvailableUpdate;
            this.noticeAuthorNickName = noticeAuthorNickName;
            this.noticeAuthorImgUrl = noticeAuthorImgUrl;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class NoticeListDto{
        private Long id;
        private String title;
        private String content;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime updatedAt;
        private String imgUrl;

        @Builder
        public NoticeListDto(Long id, String title, String content, LocalDateTime updatedAt) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.updatedAt = updatedAt;
        }
    }
}
