package com.architecture.archi.db.entity.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserEntity is a Querydsl query type for UserEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserEntity extends EntityPathBase<UserEntity> {

    private static final long serialVersionUID = 840266861L;

    public static final QUserEntity userEntity = new QUserEntity("userEntity");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final EnumPath<com.architecture.archi.common.enumobj.BooleanFlag> delYn = createEnum("delYn", com.architecture.archi.common.enumobj.BooleanFlag.class);

    public final StringPath email = createString("email");

    public final StringPath id = createString("id");

    public final StringPath nickName = createString("nickName");

    public final StringPath pw = createString("pw");

    public final EnumPath<com.architecture.archi.common.enumobj.RoleType> role = createEnum("role", com.architecture.archi.common.enumobj.RoleType.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QUserEntity(String variable) {
        super(UserEntity.class, forVariable(variable));
    }

    public QUserEntity(Path<? extends UserEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserEntity(PathMetadata metadata) {
        super(UserEntity.class, metadata);
    }

}

