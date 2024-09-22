package com.architecture.archi.db.repository.comment;

import com.architecture.archi.common.enumobj.BooleanFlag;
import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.error.ExceptionCode;
import com.architecture.archi.content.comment.model.CommentModel;
import com.architecture.archi.content.content.model.ContentModel;
import com.architecture.archi.db.entity.comment.CommentEntity;
import com.architecture.archi.db.entity.comment.QCommentEntity;
import com.architecture.archi.db.entity.content.ContentEntity;
import com.architecture.archi.db.entity.content.QContentEntity;
import com.architecture.archi.db.entity.like.CommentLikeEntity;
import com.architecture.archi.db.entity.like.QCommentLikeEntity;
import com.architecture.archi.db.entity.user.QUserEntity;
import com.architecture.archi.db.entity.user.UserFileEntity;
import com.architecture.archi.db.repository.file.FileDao;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.xml.stream.events.Comment;
import java.util.*;
import java.util.stream.Collectors;

import static com.querydsl.core.group.GroupBy.groupBy;

@RequiredArgsConstructor
@Slf4j
@Repository
public class CommentDao {

    private final JPAQueryFactory jpaQueryFactory;

    private final FileDao fileDao;

    private final QUserEntity qUserEntity = QUserEntity.userEntity;
    private final QCommentEntity qCommentEntity = QCommentEntity.commentEntity;

    private final QCommentLikeEntity qCommentLikeEntity = QCommentLikeEntity.commentLikeEntity;

    private final QContentEntity qContentEntity = QContentEntity.contentEntity;

    public CommentEntity findComment(Long id) throws CustomException {
        return Optional.ofNullable(
                        jpaQueryFactory
                                .selectFrom(qCommentEntity)
                                .leftJoin(qCommentEntity.user, qUserEntity).fetchJoin()
                                .where(qCommentEntity.id.eq(id))
                                .fetchOne()

                )
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_EXIST, String.format("Comment [%s] is null", id)));
    }

    public List<CommentEntity> findComments(List<Long> ids) throws CustomException {
        return Optional.ofNullable(
                        jpaQueryFactory
                                .selectFrom(qCommentEntity)
                                .leftJoin(qCommentEntity.user, qUserEntity).fetchJoin()
                                .where(qCommentEntity.id.in(ids))
                                .fetch()

                )
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_EXIST, String.format("Comments is null")));
    }

    public List<CommentModel.CommentDto> findCommentsByContent(Long contentId, String userId) {
        QCommentEntity qCommentEntity = QCommentEntity.commentEntity;
        QCommentEntity qChildCommentEntity = new QCommentEntity("childComment");

        QUserEntity qUserEntity2 = new QUserEntity("UserEntity2");

        QUserEntity qLikeUserEntity = new QUserEntity("qLikeUserEntity");

        List<CommentEntity> parentCommentEntityList = jpaQueryFactory
                .selectFrom(qCommentEntity)
                .leftJoin(qCommentEntity.user, qUserEntity).fetchJoin()
                .leftJoin(qCommentEntity.commentLikes, qCommentLikeEntity).fetchJoin()
                .leftJoin(qCommentLikeEntity.user, qLikeUserEntity).fetchJoin()
                .where(qCommentEntity.content.id.eq(contentId)
                        .and(qCommentEntity.parent.isNull()) // 부모 댓글만 조회 // 삭제되지 않은 댓글만 조회
                )
                .orderBy(qCommentEntity.createdAt.asc())
                .fetch();

        List<CommentModel.CommentDto> parentComments = new ArrayList<>();

        List<String> parentUserIds = parentCommentEntityList.stream()
                .map(commentEntity -> commentEntity.getUser().getId())
                .toList();

        Map<String, String> parentUserFileUrlList = fileDao.findFileUrlByUserIds(parentUserIds);

        // 부모 댓글 ID 리스트 생성
        List<Long> parentCommentIds = new ArrayList<>();
        for (CommentEntity commentEntity : parentCommentEntityList) {
            parentCommentIds.add(commentEntity.getId());
            parentComments.add(convertToDto(commentEntity, userId, parentUserFileUrlList));
        }

        List<CommentEntity> childrenCommentEntityList = jpaQueryFactory
                .selectFrom(qChildCommentEntity)
                .leftJoin(qChildCommentEntity.parent, qCommentEntity).fetchJoin()
                .leftJoin(qChildCommentEntity.user, qUserEntity).fetchJoin()
                .leftJoin(qChildCommentEntity.sendUser, qUserEntity2).fetchJoin()
                .leftJoin(qChildCommentEntity.commentLikes, qCommentLikeEntity).fetchJoin()
                .leftJoin(qCommentLikeEntity.user, qLikeUserEntity).fetchJoin()
                .where(qChildCommentEntity.content.id.eq(contentId)
                        .and(qChildCommentEntity.parent.id.in(parentCommentIds)) // 부모 댓글만 조회 // 삭제되지 않은 댓글만 조회
                )
                .orderBy(qChildCommentEntity.createdAt.asc())
                .fetch();

        // 4. 자식 댓글을 부모 댓글 ID로 그룹화
        Map<Long, List<CommentModel.CommentDto>> childrenCommentsMap = new HashMap<>();

        List<String> childrenUserIds = childrenCommentEntityList.stream()
                .map(commentEntity -> commentEntity.getUser().getId())
                .toList();

        Map<String, String> childrenUserFileUrlList = fileDao.findFileUrlByUserIds(childrenUserIds);

        for (CommentEntity commentEntity : childrenCommentEntityList) {
            Long parentId = commentEntity.getParent().getId();
            // parentId에 해당하는 리스트를 가져오거나, 없으면 새로운 리스트 생성
            List<CommentModel.CommentDto> commentList = childrenCommentsMap.computeIfAbsent(parentId, k -> new ArrayList<>());

            // 새로운 CommentDto 객체를 리스트에 추가
            CommentModel.CommentDto commentDto = convertToDto(commentEntity, userId, childrenUserFileUrlList);
            commentDto.setParentCommentId(commentEntity.getParent().getId());
            if(commentEntity.getSendUser() != null){
                commentDto.setSendUserNickName(commentEntity.getSendUser().getNickName());
            }
            commentList.add(commentDto);
        }

        // 5. 부모 댓글에 자식 댓글을 매핑
        parentComments.forEach(parentComment -> {
            List<CommentModel.CommentDto> children = childrenCommentsMap.getOrDefault(parentComment.getId(), new ArrayList<>());
            parentComment.getChildren().addAll(children);
        });

        return parentComments;
    }

    private CommentModel.CommentDto convertToDto(CommentEntity commentEntity, String userId, Map<String, String> parentUserFileUrlList) {
        List<String> likeUserIds = new ArrayList<>();
        for (CommentLikeEntity commentLike : commentEntity.getCommentLikes()) {
            likeUserIds.add(commentLike.getUser().getId());
        }

        return CommentModel.CommentDto.builder()
                .id(commentEntity.getId())
                .comment(commentEntity.getComment())
                .delYn(commentEntity.getDelYn())
                .userNickName(commentEntity.getUser().getNickName())
                .commentAuthorImgUrl(parentUserFileUrlList.getOrDefault(commentEntity.getUser().getId(), null))
                .createdAt(commentEntity.getCreatedAt())
                .updatedAt(commentEntity.getUpdatedAt())
                .like((long) commentEntity.getCommentLikes().size())
                .likeUserIds(likeUserIds)
                .isContentAuthor(commentEntity.getIsContentAuthor())
                .isWriteUser(userId != null ? commentEntity.getUser().getId().equals(userId) : false)
                .children(new ArrayList<>())
                .build();
    }

    public CommentEntity findSimpleComment(Long id) throws CustomException {
        return Optional.ofNullable(
                        jpaQueryFactory
                                .selectFrom(qCommentEntity)
                                .where(qCommentEntity.id.eq(id)
                                        .and(qCommentEntity.delYn.eq(BooleanFlag.N)))
                                .fetchOne()

                )
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_EXIST, String.format("해당 코멘트([%s])는 삭제되었거나, 없는 코멘트 입니다.", id)));
    }

//    public long updateComment(Long commentId, String comment) {
//        return jpaQueryFactory
//                .update(qCommentEntity)
//                .set(qCommentEntity.comment, comment)
//                .where(qCommentEntity.id.eq(commentId))
//                .execute();
//    }

    public Page<CommentModel.UserCommentDto> findUserCommentsPagingByUserId(String userId, Pageable pageable) throws CustomException {
        try{
            List<CommentModel.UserCommentDto> commentListDtoList = jpaQueryFactory
                    .select(
                            Projections.constructor(CommentModel.UserCommentDto.class,
                                    qCommentEntity.id,
                                    qCommentEntity.content.id,
                                    qCommentEntity.content.title,
                                    qCommentEntity.comment,
                                    qCommentEntity.createdAt,
                                    qCommentEntity.updatedAt,
                                    // 서브쿼리를 사용하여 해당 content의 좋아요 수를 계산
                                    JPAExpressions
                                            .select(qCommentLikeEntity.count())
                                            .from(qCommentLikeEntity)
                                            .where(qCommentLikeEntity.comment.id.eq(qCommentEntity.id))
                            )

                    )
                    .from(qCommentEntity)
                    .where(
                            qCommentEntity.delYn.eq(BooleanFlag.N)
                                    .and(qCommentEntity.user.id.eq(userId))
                    )
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .orderBy(qCommentEntity.createdAt.desc())
                    .fetch();

            long total = jpaQueryFactory
                    .selectFrom(qCommentEntity)
                    .where(
                            qCommentEntity.delYn.eq(BooleanFlag.N)
                                    .and(qCommentEntity.user.id.eq(userId))
                    )
                    .stream().count();
            return new PageImpl<>(commentListDtoList, pageable, total);
        } catch(Exception e){
            log.error(e.getMessage());
            throw new CustomException(ExceptionCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
