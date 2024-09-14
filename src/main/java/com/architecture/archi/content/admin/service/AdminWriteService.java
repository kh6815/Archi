package com.architecture.archi.content.admin.service;

import com.architecture.archi.common.enumobj.BooleanFlag;
import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.error.ExceptionCode;
import com.architecture.archi.config.security.user.CustomUserDetails;
import com.architecture.archi.content.admin.model.AdminModel;
import com.architecture.archi.content.content.model.ContentModel;
import com.architecture.archi.db.entity.category.CategoryEntity;
import com.architecture.archi.db.entity.content.ContentEntity;
import com.architecture.archi.db.entity.content.ContentFileEntity;
import com.architecture.archi.db.entity.file.FileEntity;
import com.architecture.archi.db.entity.notice.NoticeEntity;
import com.architecture.archi.db.entity.notice.NoticeFileEntity;
import com.architecture.archi.db.entity.user.UserEntity;
import com.architecture.archi.db.repository.category.CategoryRepository;
import com.architecture.archi.db.repository.file.FileRepository;
import com.architecture.archi.db.repository.notice.NoticeDao;
import com.architecture.archi.db.repository.notice.NoticeFileRepository;
import com.architecture.archi.db.repository.notice.NoticeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminWriteService {

    private final CategoryRepository categoryRepository;
    private final NoticeRepository noticeRepository;
    private final NoticeFileRepository noticeFileRepository;
    private final FileRepository fileRepository;
    private final NoticeDao noticeDao;

    @Transactional(rollbackFor = Exception.class)
    public Boolean createCategory(AdminModel.AddCategoryReq addCategoryReq, CustomUserDetails userDetails) throws CustomException {
        // 등록시 주의할점 addCategoryReq.parentsId 0인 경우는 부모카테고리를 가져올 필요 없음

        CategoryEntity parentsCategory = null;

        if(addCategoryReq.getParentsId() != 0){
            parentsCategory = categoryRepository.findById(addCategoryReq.getParentsId())
                    .orElseThrow(() -> new CustomException(ExceptionCode.NOT_EXIST, "존재하지 않은 상단 카테고리입니다."));

            if(parentsCategory.getActiveYn().equals(BooleanFlag.N)){
                throw new CustomException(ExceptionCode.NOT_EXIST, "활성화되지 않은 카테고리입니다.");
            }
        }

        CategoryEntity categoryEntity = CategoryEntity.builder()
                .parentsCategory(parentsCategory)
                .categoryName(addCategoryReq.getCategoryName())
                .createUser(userDetails.getUsername())
                .build();

        categoryRepository.save(categoryEntity);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean updateCategoryName(Long categoryId, String categoryName, CustomUserDetails userDetails) throws CustomException {
        CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_EXIST, "존재하지 않는 카테고리"));

        categoryEntity.updateCategoryName(categoryName, userDetails.getUsername());
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteCategory(Long categoryId, CustomUserDetails userDetails) throws CustomException {
        CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_EXIST, "존재하지 않는 카테고리"));

        categoryEntity.updateActive(BooleanFlag.N, userDetails.getUsername());
        return true;
    }


    @Transactional(rollbackFor = Exception.class)
    public Long createNotice(AdminModel.AddNoticeReq addNoticeReq, CustomUserDetails userDetails) throws CustomException {
        // 유저 가져오기
        UserEntity userEntity = userDetails.getUser();

        NoticeEntity notice = NoticeEntity.builder()
                .user(userEntity)
                .title(addNoticeReq.getTitle())
                .content(addNoticeReq.getContent())
                .build();

        NoticeEntity noticeEntity = noticeRepository.save(notice);

        List<FileEntity> fileEntityList = fileRepository.findByIdIn(addNoticeReq.getImgFileIdList());
        List<NoticeFileEntity> noticeFileEntityList = new ArrayList<>();

        for (FileEntity fileEntity : fileEntityList) {
            noticeFileEntityList.add(NoticeFileEntity.builder()
                    .notice(noticeEntity)
                    .file(fileEntity)
                    .build());
        }

        noticeFileRepository.saveAll(noticeFileEntityList);

        return noticeEntity.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean updateNotice(AdminModel.UpdateNoticeReq updateNoticeReq, CustomUserDetails userDetails) throws CustomException {
        NoticeEntity noticeEntity = noticeDao.findNotice(updateNoticeReq.getId());

        if(!userDetails.getUsername().equals(noticeEntity.getUser().getId())){
            throw new CustomException(ExceptionCode.INVALID, "작성자가 아닌 사람은 게시글을 업데이트 할 수 없습니다.");
        }

        String newTitle = noticeEntity.getTitle();
        String newContent = noticeEntity.getContent();

        if(!noticeEntity.getTitle().equals(updateNoticeReq.getTitle())){
            newTitle = updateNoticeReq.getTitle();
        }

        if(!noticeEntity.getContent().equals(updateNoticeReq.getContent())){
            newContent = updateNoticeReq.getContent();
        }

        noticeEntity.updateContent(newTitle, newContent);


        // getImgFileIdList에서 추가된거는 ContentFileEntity를 만들어서 saveAll하고, 지워진 거는 ContentFileEntity를 비활성화하는 로직
        List<NoticeFileEntity> noticeFileEntityList = noticeDao.findNoticeFileByNoticeId(noticeEntity.getId());
        List<Long> existingFileIds = noticeFileEntityList.stream().map(NoticeFileEntity::getId).toList();
        List<Long> newFileIds = updateNoticeReq.getImgFileIdList();

        // 추가된 ID 리스트
        List<Long> addedFileIds = newFileIds.stream()
                .filter(id -> !existingFileIds.contains(id))
                .collect(Collectors.toList());

        // 삭제된 ContentFileEntity 리스트
        List<NoticeFileEntity> removedNoticeFileEntity = noticeFileEntityList.stream()
                .filter(id -> !newFileIds.contains(id))
                .toList();


        if(!addedFileIds.isEmpty()){

            List<NoticeFileEntity> newNoticeFileEntityList = new ArrayList<>();
            List<FileEntity> fileEntityList = fileRepository.findByIdIn(addedFileIds);

            for (FileEntity fileEntity : fileEntityList) {
                newNoticeFileEntityList.add(NoticeFileEntity.builder()
                        .notice(noticeEntity)
                        .file(fileEntity)
                        .build());
            }

            noticeFileRepository.saveAll(newNoticeFileEntityList);
        }

        if(!removedNoticeFileEntity.isEmpty()) {
            // removedFileIds에 포함되는 해당하는 contentFile 모두 비활성화
            removedNoticeFileEntity.forEach(NoticeFileEntity::delete);
            noticeFileRepository.saveAll(removedNoticeFileEntity);
        }

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteNotice(AdminModel.DeleteNoticeReq deleteNoticeReq) throws CustomException {
        List<NoticeEntity> noticeEntityList = noticeRepository.findByIdIn(deleteNoticeReq.getIds());

        noticeEntityList.forEach(NoticeEntity::deleteContent);
        noticeRepository.saveAll(noticeEntityList);

        // 관련된 모든 contentFileEntity도 비활성화
        List<Long> deleteNoticeIds = noticeEntityList.stream().map(NoticeEntity::getId).toList();
        List<NoticeFileEntity> toBeDeleteNoticeFileEntityList = noticeDao.findNoticeFileByNoticeIds(deleteNoticeIds);

        toBeDeleteNoticeFileEntityList.forEach(NoticeFileEntity::delete);
        noticeFileRepository.saveAll(toBeDeleteNoticeFileEntityList);

        return true;
    }
}
