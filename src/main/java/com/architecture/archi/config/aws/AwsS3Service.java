package com.architecture.archi.config.aws;

import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.error.ExceptionCode;
import io.awspring.cloud.s3.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class AwsS3Service {
    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${spring.cloud.aws.folder.photo}")
    private String photoFolder;

//    public String uploadFile(MultipartFile multipartFile) throws CustomException {
//
//        if(multipartFile.isEmpty()) {
//            log.info("image is null");
//            return "";
//        }
//
//        String fileName = getFileName(multipartFile);
//
//        try {
//            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//                    .bucket(bucketName)
//                    .contentType(multipartFile.getContentType())
//                    .contentLength(multipartFile.getSize())
//                    .key(fileName)
//                    .build();
//            RequestBody requestBody = RequestBody.fromBytes(multipartFile.getBytes());
//            s3Client.putObject(putObjectRequest, requestBody);
//        } catch (IOException e) {
//            log.error("cannot upload image",e);
//            throw new CustomException(ExceptionCode.INTERNAL_SERVER_ERROR, "서버오류로 인해 이미지를 업로드 하지 못했습니다.");
//        }
//        GetUrlRequest getUrlRequest = GetUrlRequest.builder()
//                .bucket(bucketName)
//                .key(fileName)
//                .build();
//
//        return s3Client.utilities().getUrl(getUrlRequest).toString();
//    }

    public String uploadFile(MultipartFile multipartFile, String fileName) throws CustomException {

        if(multipartFile.isEmpty()) {
            log.info("image is null");
            return "";
        }

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .contentType(multipartFile.getContentType())
                    .contentLength(multipartFile.getSize())
                    .key(fileName) // 미리 생성된 fileName을 사용
                    .build();
            RequestBody requestBody = RequestBody.fromBytes(multipartFile.getBytes());
            s3Client.putObject(putObjectRequest, requestBody);
        } catch (IOException e) {
            log.error("cannot upload image",e);
            throw new CustomException(ExceptionCode.INTERNAL_SERVER_ERROR, "서버오류로 인해 이미지를 업로드 하지 못했습니다.");
        }

        GetUrlRequest getUrlRequest = GetUrlRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        return s3Client.utilities().getUrl(getUrlRequest).toString(); // S3 파일 URL 반환
    }

    public String getFileName(MultipartFile multipartFile) {
        if(multipartFile.isEmpty()) return "";
        return StorageUtils.createStoragePath(multipartFile.getOriginalFilename(), photoFolder);
    }

    //파일 삭제
    public void deleteFile(String filePath){
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(filePath)
                .build();
        s3Client.deleteObject(request);
    }
}
