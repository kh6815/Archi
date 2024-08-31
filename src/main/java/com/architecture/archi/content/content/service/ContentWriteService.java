package com.architecture.archi.content.content.service;

import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.error.ExceptionCode;
import com.architecture.archi.config.security.user.CustomUserDetails;
import com.architecture.archi.content.content.model.ContentModel;
import com.architecture.archi.db.entity.category.CategoryEntity;
import com.architecture.archi.db.entity.content.ContentEntity;
import com.architecture.archi.db.entity.content.ContentFileEntity;
import com.architecture.archi.db.entity.file.FileEntity;
import com.architecture.archi.db.entity.like.ContentLikeEntity;
import com.architecture.archi.db.entity.user.UserEntity;
import com.architecture.archi.db.repository.category.CategoryRepository;
import com.architecture.archi.db.repository.content.ContentDao;
import com.architecture.archi.db.repository.content.ContentFileRepository;
import com.architecture.archi.db.repository.content.ContentRepository;
import com.architecture.archi.db.repository.file.FileRepository;
import com.architecture.archi.db.repository.like.ContentLikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ContentWriteService {

    private final ContentRepository contentRepository;
    private final ContentFileRepository contentFileRepository;
    private final FileRepository fileRepository;
    private final CategoryRepository categoryRepository;
    private final ContentDao contentDao;
    private final ContentLikeRepository contentLikeRepository;

    @Transactional(rollbackFor = Exception.class)
    public Long createContent(ContentModel.AddContentReq addContentReq, CustomUserDetails userDetails) throws CustomException {
        // 1. 유저, 카테고리 가져오기
        // 2. req에 있는 파일 엔티티들 가져오기 -> in절로 한번에 가져오기
        // 3. content save
        // 4. contentFile sava하는데 여러개의 엔티티를 한번에 savaAll(savaAll은 최대 만건 단위로 끊어서 저장해주는 것이 좋다)

        // 유저 가져오기
        UserEntity userEntity = userDetails.getUser();

        // 카테고리 가져오기
        CategoryEntity categoryEntity = categoryRepository.findById(addContentReq.getCategoryId())
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_EXIST, "존재하지 않는 카테고리"));

        ContentEntity content = ContentEntity.builder()
                .user(userEntity)
                .category(categoryEntity)
                .title(addContentReq.getTitle())
                .content(addContentReq.getContent())
                .build();

        ContentEntity contentEntity = contentRepository.save(content);

        List<FileEntity> fileEntityList = fileRepository.findByIdIn(addContentReq.getImgFileIdList());
        List<ContentFileEntity> contentFileEntityList = new ArrayList<>();

        for (FileEntity fileEntity : fileEntityList) {
            contentFileEntityList.add(ContentFileEntity.builder()
                            .content(contentEntity)
                            .file(fileEntity)
                            .build());
        }

        contentFileRepository.saveAll(contentFileEntityList);

        return contentEntity.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean updateContent(CustomUserDetails userDetails, ContentModel.UpdateContentReq updateContentReq) throws CustomException {
        ContentEntity contentEntity = contentDao.findContent(updateContentReq.getId());

        if(!userDetails.getUsername().equals(contentEntity.getUser().getId())){
            throw new CustomException(ExceptionCode.INVALID, "작성자가 아닌 사람은 게시글을 업데이트 할 수 없습니다.");
        }

        String newTitle = contentEntity.getTitle();
        String newContent = contentEntity.getContent();
        CategoryEntity newCategory = contentEntity.getCategory();

        if(!contentEntity.getTitle().equals(updateContentReq.getTitle())){
            newTitle = updateContentReq.getTitle();
        }

        if(!contentEntity.getContent().equals(updateContentReq.getContent())){
            newContent = updateContentReq.getContent();
        }

        if(!contentEntity.getCategory().getId().equals(updateContentReq.getCategoryId())){
            CategoryEntity category = categoryRepository.findById(updateContentReq.getCategoryId())
                    .orElseThrow(() -> new CustomException(ExceptionCode.NOT_EXIST, "존재하지 않는 카테고리"));

            newCategory = category;
        }

        contentEntity.updateContent(newTitle, newContent, newCategory);


        // getImgFileIdList에서 추가된거는 ContentFileEntity를 만들어서 saveAll하고, 지워진 거는 ContentFileEntity를 비활성화하는 로직
        List<ContentFileEntity> contentFileEntityList = contentDao.findContentFileByContentId(contentEntity.getId());
        List<Long> existingFileIds = contentFileEntityList.stream().map(ContentFileEntity::getId).toList();
        List<Long> newFileIds = updateContentReq.getImgFileIdList();

        // 추가된 ID 리스트
        List<Long> addedFileIds = newFileIds.stream()
                .filter(id -> !existingFileIds.contains(id))
                .collect(Collectors.toList());

        // 삭제된 ContentFileEntity 리스트
        List<ContentFileEntity> removedContentFileEntity = contentFileEntityList.stream()
                .filter(id -> !newFileIds.contains(id))
                .toList();


        if(!addedFileIds.isEmpty()){

            List<ContentFileEntity> newContentFileEntityList = new ArrayList<>();
            List<FileEntity> fileEntityList = fileRepository.findByIdIn(addedFileIds);

            for (FileEntity fileEntity : fileEntityList) {
                newContentFileEntityList.add(ContentFileEntity.builder()
                        .content(contentEntity)
                        .file(fileEntity)
                        .build());
            }

            contentFileRepository.saveAll(newContentFileEntityList);
        }

        if(!removedContentFileEntity.isEmpty()) {
            // removedFileIds에 포함되는 해당하는 contentFile 모두 비활성화
            removedContentFileEntity.forEach(ContentFileEntity::delete);
            contentFileRepository.saveAll(removedContentFileEntity);
        }

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteContent(ContentModel.DeleteContentReq deleteContentReq) throws CustomException {
        List<ContentEntity> contentEntityList = contentRepository.findByIdIn(deleteContentReq.getIds());

        contentEntityList.forEach(ContentEntity::deleteContent);
        contentRepository.saveAll(contentEntityList);

        // 관련된 모든 contentFileEntity도 비활성화
        List<Long> deleteContentIds = contentEntityList.stream().map(ContentEntity::getId).toList();
        List<ContentFileEntity> toBeDeleteContentFileEntityList = contentDao.findContentFileByContentIds(deleteContentIds);

        toBeDeleteContentFileEntityList.forEach(ContentFileEntity::delete);
        contentFileRepository.saveAll(toBeDeleteContentFileEntityList);

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean updateLike(ContentModel.UpdateLikeReq updateLikeReq, CustomUserDetails userDetails) throws CustomException {
        Optional<ContentLikeEntity> likeEntityOptional = contentLikeRepository.findByUser_IdAndContent_Id(userDetails.getUsername(), updateLikeReq.getContentId());

        if(likeEntityOptional.isPresent()){
            contentLikeRepository.delete(likeEntityOptional.get());
        } else {
            ContentEntity contentEntity = contentDao.findContent(updateLikeReq.getContentId());

            ContentLikeEntity contentLikeEntity = ContentLikeEntity.builder()
                    .user(userDetails.getUser())
                    .content(contentEntity)
                    .build();

            contentLikeRepository.save(contentLikeEntity);
        }

        return true;
    }

}
