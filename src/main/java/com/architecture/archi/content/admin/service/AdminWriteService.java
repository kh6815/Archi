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
import com.architecture.archi.db.repository.file.FileDao;
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
    private final FileDao fileDao;

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

        // 이미지 추가 하고
        if(!updateNoticeReq.getAddFileIdList().isEmpty()){
            List<FileEntity> saveFileList = fileDao.findFileListByFileIds(updateNoticeReq.getAddFileIdList());

            List<NoticeFileEntity> willSaveNoticeFileEntityList = new ArrayList<>();
            for (FileEntity file : saveFileList) {
                willSaveNoticeFileEntityList.add(NoticeFileEntity.builder()
                        .notice(noticeEntity)
                        .file(file)
                        .build());
            }

            noticeFileRepository.saveAll(willSaveNoticeFileEntityList);
        }

        // 이미지 업데이트 하고
        if(!updateNoticeReq.getUpdateFileMap().isEmpty()){
            List<Long> updateFileIdList = new ArrayList<>(updateNoticeReq.getUpdateFileMap().keySet());


            List<FileEntity> findUpdateFileEntityList = fileDao.findFileListByFileIds(updateFileIdList);

            for (FileEntity fileEntity : findUpdateFileEntityList) {
                fileEntity.updateName(updateNoticeReq.getUpdateFileMap().get(fileEntity.getId()));
            }
        }

        // 이미지 삭제 하고 -> noticeFileEntity만 delYn = Y처리하고 나중에 배치서버에서 noticeFile, file, a3 모두 삭제
        if(!updateNoticeReq.getDeleteFileIdList().isEmpty()){
            List<NoticeFileEntity> deleteNoticeFileEntityList = noticeDao.findNoticeFileByFileIds(updateNoticeReq.getDeleteFileIdList());

            noticeDao.updateDelYnContentNoticeListByNoticeIds(deleteNoticeFileEntityList.stream()
                    .map(NoticeFileEntity::getId)
                    .toList());
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
