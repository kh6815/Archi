package com.architecture.archi.db.repository.user;

import com.architecture.archi.common.enumobj.BooleanFlag;
import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.error.ExceptionCode;
import com.architecture.archi.db.entity.auth.QTokenPairEntity;
import com.architecture.archi.db.entity.file.QFileEntity;
import com.architecture.archi.db.entity.user.QUserEntity;
import com.architecture.archi.db.entity.user.QUserFileEntity;
import com.architecture.archi.db.entity.user.UserEntity;
import com.architecture.archi.db.entity.user.UserFileEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.querydsl.core.group.GroupBy.groupBy;

@RequiredArgsConstructor
@Repository
public class UserDao {
    private final JPAQueryFactory jpaQueryFactory;

    private final QUserEntity qUserEntity = QUserEntity.userEntity;

    private final QFileEntity qFileEntity = QFileEntity.fileEntity;

    private final QUserFileEntity qUserFileEntity = QUserFileEntity.userFileEntity;

    private final QTokenPairEntity qTokenPairEntity = QTokenPairEntity.tokenPairEntity;

    public Optional<UserFileEntity> findUserFileByUserId(String userId){
        return
                Optional.ofNullable(
                        jpaQueryFactory
                                .selectFrom(qUserFileEntity)
                                .where(qUserFileEntity.user.id.eq(userId)
                                        .and(qUserFileEntity.delYn.eq(BooleanFlag.N))
                                )
                                .fetchOne()
                );
    }

    public Optional<UserFileEntity> findUserFileWithFileByUserId(String userId){
        return
                Optional.ofNullable(
                        jpaQueryFactory
                                .selectFrom(qUserFileEntity)
                                .leftJoin(qUserFileEntity.file, qFileEntity).fetchJoin()
                                .where(qUserFileEntity.user.id.eq(userId)
                                        .and(qUserFileEntity.delYn.eq(BooleanFlag.N))
                                )
                                .fetchOne()
                );
    }
}
