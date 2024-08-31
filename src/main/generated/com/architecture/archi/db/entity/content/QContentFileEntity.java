package com.architecture.archi.db.entity.content;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QContentFileEntity is a Querydsl query type for ContentFileEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QContentFileEntity extends EntityPathBase<ContentFileEntity> {

    private static final long serialVersionUID = -509673665L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QContentFileEntity contentFileEntity = new QContentFileEntity("contentFileEntity");

    public final QContentEntity content;

    public final EnumPath<com.architecture.archi.common.enumobj.BooleanFlag> delYn = createEnum("delYn", com.architecture.archi.common.enumobj.BooleanFlag.class);

    public final com.architecture.archi.db.entity.file.QFileEntity file;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QContentFileEntity(String variable) {
        this(ContentFileEntity.class, forVariable(variable), INITS);
    }

    public QContentFileEntity(Path<? extends ContentFileEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QContentFileEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QContentFileEntity(PathMetadata metadata, PathInits inits) {
        this(ContentFileEntity.class, metadata, inits);
    }

    public QContentFileEntity(Class<? extends ContentFileEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.content = inits.isInitialized("content") ? new QContentEntity(forProperty("content"), inits.get("content")) : null;
        this.file = inits.isInitialized("file") ? new com.architecture.archi.db.entity.file.QFileEntity(forProperty("file")) : null;
    }

}

