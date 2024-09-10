package com.architecture.archi.content.file.service;

import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.error.ExceptionCode;
import com.architecture.archi.config.aws.AwsS3Service;
import com.architecture.archi.config.aws.Constants;
import com.architecture.archi.config.aws.StorageUtils;
import com.architecture.archi.content.file.model.FileModel;
import com.architecture.archi.db.entity.file.FileEntity;
import com.architecture.archi.db.repository.file.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    private final AwsS3Service awsS3Service;

    @Value("${spring.cloud.aws.folder.photo}")
    private String photoFolder;

//    public FileModel.FileRes saveFile(MultipartFile file) throws CustomException {
//        String fileName = file.getOriginalFilename();
//
//        if (!StringUtils.hasText(fileName)) {
//            throw new CustomException(ExceptionCode.BAD_REQUEST);
//        }
//
//        String originName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.lastIndexOf("."));
//        String path = StorageUtils.createStoragePath(fileName, photoFolder);
//        String ext = path.substring(path.lastIndexOf(".") + 1);
//        String name = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
//        Long size = file.getSize();
//
//        if (!Arrays.asList(Constants.ARR_VALID_FILE_EXT).contains(ext.toLowerCase())) {
//            throw new CustomException(ExceptionCode.BAD_REQUEST, ext.toLowerCase() + " 해당 종류의 파일은 업로드 할 수 없습니다.");
//        }
//
//        if (size > Constants.BASE_MAX_FILE_SIZE) {
//            throw new CustomException(ExceptionCode.BAD_REQUEST, "10MB 이하의 파일만 업로드 할 수 있습니다.");
//        }
//
//        String s3Url = awsS3Service.uploadFile(file);
//
//        FileEntity fileEntity= FileEntity.builder()
//                .originName(originName)
//                .path(path)
//                .name(name)
//                .size(size)
//                .ext(ext)
//                .url(s3Url)
//                .build();
//
//        FileEntity saveFileEntity = fileRepository.save(fileEntity);
//
//        return FileModel.FileRes.builder()
//                .fileId(saveFileEntity.getId())
//                .fileUrl(saveFileEntity.getUrl())
//                .build();
//    }

    public FileModel.FileRes saveFile(MultipartFile file) throws CustomException {
        String fileName = file.getOriginalFilename();

        if (!StringUtils.hasText(fileName)) {
            throw new CustomException(ExceptionCode.BAD_REQUEST);
        }

        // S3에 저장될 파일 이름과 경로를 미리 생성
        String path = StorageUtils.createStoragePath(fileName, photoFolder); // 파일의 경로 생성
        String name = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf(".")); // 파일 이름 (확장자 제외)
        String ext = path.substring(path.lastIndexOf(".") + 1); // 파일 확장자
        Long size = file.getSize();

        // 파일 확장자 검증
        if (!Arrays.asList(Constants.ARR_VALID_FILE_EXT).contains(ext.toLowerCase())) {
            throw new CustomException(ExceptionCode.BAD_REQUEST, ext.toLowerCase() + " 해당 종류의 파일은 업로드 할 수 없습니다.");
        }

        // 파일 크기 검증
        if (size > Constants.BASE_MAX_FILE_SIZE) {
            throw new CustomException(ExceptionCode.BAD_REQUEST, "10MB 이하의 파일만 업로드 할 수 있습니다.");
        }

        // S3에 파일 업로드 (path를 그대로 사용)
        String s3Url = awsS3Service.uploadFile(file, path); // path를 전달하여 동일한 파일명 사용

        // DB에 저장될 파일 정보 생성
        FileEntity fileEntity= FileEntity.builder()
                .originName(fileName) // 원본 파일 이름
                .path(path) // S3에 저장된 경로
                .name(name) // 파일 이름 (확장자 제외)
                .size(size)
                .ext(ext)
                .url(s3Url) // S3 URL
                .build();

        FileEntity saveFileEntity = fileRepository.save(fileEntity);

        return FileModel.FileRes.builder()
                .fileId(saveFileEntity.getId())
                .fileUrl(saveFileEntity.getUrl())
                .build();
    }


    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteFile(Long fileId) throws CustomException {
        FileEntity fileEntity = fileRepository.findById(fileId)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_EXIST, "존재하지 않는 파일"));

        awsS3Service.deleteFile(fileEntity.getPath());

        return true;
    }
}
