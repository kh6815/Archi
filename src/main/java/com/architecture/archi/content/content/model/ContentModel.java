package com.architecture.archi.content.content.model;

import com.architecture.archi.common.enumobj.BooleanFlag;
import com.architecture.archi.content.category.model.CategoryModel;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

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

    @Getter
    public static class UpdateContentReq{
        private Long id;
        private Long categoryId;
        private String title;
        private String content;
        private List<Long> imgFileIdList;
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
        private String title;
        private String content;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime updatedAt;
        private Boolean isAvailableUpdate;
        private Long like;

        @Builder
        public ContentDto(Long id, String categoryName, String title, String content, LocalDateTime updatedAt, Boolean isAvailableUpdate, Long like) {
            this.id = id;
            this.categoryName = categoryName;
            this.title = title;
            this.content = content;
            this.updatedAt = updatedAt;
            this.isAvailableUpdate = isAvailableUpdate;
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

        @Builder
        public ContentListDto(Long id, String categoryName, String title, String content, LocalDateTime updatedAt, Long like) {
            this.id = id;
            this.categoryName = categoryName;
            this.title = title;
            this.content = content;
            this.updatedAt = updatedAt;
            this.like = like;
        }
    }
}
