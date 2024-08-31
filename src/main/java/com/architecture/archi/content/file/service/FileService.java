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

    public FileModel.FileRes saveFile(MultipartFile file) throws CustomException {
        String fileName = file.getOriginalFilename();

        if (!StringUtils.hasText(fileName)) {
            throw new CustomException(ExceptionCode.BAD_REQUEST);
        }

        String originName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.lastIndexOf("."));
        String path = StorageUtils.createStoragePath(fileName, photoFolder);
        String ext = path.substring(path.lastIndexOf(".") + 1);
        String name = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
        Long size = file.getSize();

        if (!Arrays.asList(Constants.ARR_VALID_FILE_EXT).contains(ext.toLowerCase())) {
            throw new CustomException(ExceptionCode.BAD_REQUEST, ext.toLowerCase() + " 해당 종류의 파일은 업로드 할 수 없습니다.");
        }

        if (size > Constants.BASE_MAX_FILE_SIZE) {
            throw new CustomException(ExceptionCode.BAD_REQUEST, "10MB 이하의 파일만 업로드 할 수 있습니다.");
        }

        String s3Url = awsS3Service.uploadFile(file);

        FileEntity fileEntity= FileEntity.builder()
                .originName(originName)
                .path(path)
                .name(name)
                .size(size)
                .ext(ext)
                .url(s3Url)
                .build();

        FileEntity saveFileEntity = fileRepository.save(fileEntity);

        return FileModel.FileRes.builder()
                .fileId(saveFileEntity.getId())
                .fileUrl(saveFileEntity.getUrl())
                .build();
    }
}
