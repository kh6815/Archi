package com.architecture.archi.content.file.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class FileModel {

    /*
    * request
    * */
//    @Getter
//    public static class FileReq {
//        @NotNull
//        private MultipartFile file;
//    }

    /*
    * response
    * */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class FileRes {
        private Long fileId;
        private String fileUrl;

        @Builder
        public FileRes(Long fileId, String fileUrl){
            this.fileId = fileId;
            this.fileUrl = fileUrl;
        }
    }
}
