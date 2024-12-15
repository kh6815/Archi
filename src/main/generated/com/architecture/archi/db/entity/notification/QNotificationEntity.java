package com.architecture.archi.db.entity.notification;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNotificationEntity is a Querydsl query type for NotificationEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNotificationEntity extends EntityPathBase<NotificationEntity> {

    private static final long serialVersionUID = 31128941L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNotificationEntity notificationEntity = new QNotificationEntity("notificationEntity");

    public final com.architecture.archi.db.entity.content.QContentEntity content;

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath message = createString("message");

    public final EnumPath<com.architecture.archi.common.enumobj.BooleanFlag> readYn = createEnum("readYn", com.architecture.archi.common.enumobj.BooleanFlag.class);

    public final com.architecture.archi.db.entity.user.QUserEntity receiver;

    public final com.architecture.archi.db.entity.user.QUserEntity sender;

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QNotificationEntity(String variable) {
        this(NotificationEntity.class, forVariable(variable), INITS);
    }

    public QNotificationEntity(Path<? extends NotificationEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNotificationEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNotificationEntity(PathMetadata metadata, PathInits inits) {
        this(NotificationEntity.class, metadata, inits);
    }

    public QNotificationEntity(Class<? extends NotificationEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.content = inits.isInitialized("content") ? new com.architecture.archi.db.entity.content.QContentEntity(forProperty("content"), inits.get("content")) : null;
        this.receiver = inits.isInitialized("receiver") ? new com.architecture.archi.db.entity.user.QUserEntity(forProperty("receiver")) : null;
        this.sender = inits.isInitialized("sender") ? new com.architecture.archi.db.entity.user.QUserEntity(forProperty("sender")) : null;
    }

}

