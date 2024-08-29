package com.architecture.archi.db.entity.category;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCategoryEntity is a Querydsl query type for CategoryEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCategoryEntity extends EntityPathBase<CategoryEntity> {

    private static final long serialVersionUID = 1213288525L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCategoryEntity categoryEntity = new QCategoryEntity("categoryEntity");

    public final EnumPath<com.architecture.archi.common.enumobj.BooleanFlag> activeYn = createEnum("activeYn", com.architecture.archi.common.enumobj.BooleanFlag.class);

    public final StringPath categoryName = createString("categoryName");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath createUser = createString("createUser");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QCategoryEntity parentsCategory;

    public final ListPath<CategoryEntity, QCategoryEntity> subCategories = this.<CategoryEntity, QCategoryEntity>createList("subCategories", CategoryEntity.class, QCategoryEntity.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final StringPath updateUser = createString("updateUser");

    public QCategoryEntity(String variable) {
        this(CategoryEntity.class, forVariable(variable), INITS);
    }

    public QCategoryEntity(Path<? extends CategoryEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCategoryEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCategoryEntity(PathMetadata metadata, PathInits inits) {
        this(CategoryEntity.class, metadata, inits);
    }

    public QCategoryEntity(Class<? extends CategoryEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.parentsCategory = inits.isInitialized("parentsCategory") ? new QCategoryEntity(forProperty("parentsCategory"), inits.get("parentsCategory")) : null;
    }

}

