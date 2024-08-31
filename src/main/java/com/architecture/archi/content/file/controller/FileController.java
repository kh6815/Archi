package com.architecture.archi.content.file.controller;

import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.model.ApiResponseModel;
import com.architecture.archi.config.security.user.CustomUserDetails;
import com.architecture.archi.content.file.model.FileModel;
import com.architecture.archi.content.file.service.FileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/file")
public class FileController {

    private final FileService fileService;

    // 파일 등록
//    @PostMapping(value = {"/add"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ApiResponseModel<FileModel.FileRes> addFile(@ModelAttribute FileModel.FileReq fileReq) throws CustomException {
//        return new ApiResponseModel<>(fileService.saveFile(fileReq.getFile()));
//    }

    @PostMapping(value = {"/add"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponseModel<FileModel.FileRes> addFile(@RequestPart("file") MultipartFile file) throws CustomException {
        return new ApiResponseModel<>(fileService.saveFile(file));
    }
}
