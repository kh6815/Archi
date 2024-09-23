package com.architecture.archi.db.repository.notification;

import com.architecture.archi.common.enumobj.BooleanFlag;
import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.error.ExceptionCode;
import com.architecture.archi.content.notification.model.NotificationModel;
import com.architecture.archi.db.entity.auth.QTokenPairEntity;
import com.architecture.archi.db.entity.content.ContentFileEntity;
import com.architecture.archi.db.entity.file.QFileEntity;
import com.architecture.archi.db.entity.notification.NotificationEntity;
import com.architecture.archi.db.entity.notification.QNotificationEntity;
import com.architecture.archi.db.entity.user.QUserEntity;
import com.architecture.archi.db.entity.user.QUserFileEntity;
import com.architecture.archi.db.entity.user.UserFileEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class NotificationDao {
    private final JPAQueryFactory jpaQueryFactory;

    private final QUserEntity qUserEntity = QUserEntity.userEntity;
    private final QNotificationEntity qNotificationEntity = QNotificationEntity.notificationEntity;

    public List<NotificationModel.NotificationListDto> findNotificationReadNByReceiverId(String userId) throws CustomException {
        return
                jpaQueryFactory
                        .select(
                                Projections.constructor(NotificationModel.NotificationListDto.class,
                                        qNotificationEntity.id,
                                        qNotificationEntity.content.id,
                                        qNotificationEntity.sender.nickName,
                                        qNotificationEntity.message,
                                        qNotificationEntity.readYn,
                                        qNotificationEntity.createdAt,
                                        qNotificationEntity.updatedAt
                                        )
                        )
                        .from(qNotificationEntity)
                        .where(qNotificationEntity.receiver.id.eq(userId)
                                .and(qNotificationEntity.readYn.eq(BooleanFlag.N))
                        )
                        .orderBy(qNotificationEntity.createdAt.desc())
                        .limit(20)
                        .fetch();
    }
}
