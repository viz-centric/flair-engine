package com.fbi.engine.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QConnectionType is a Querydsl query type for ConnectionType
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QConnectionType extends EntityPathBase<ConnectionType> {

    private static final long serialVersionUID = 445554948L;

    public static final QConnectionType connectionType = new QConnectionType("connectionType");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final StringPath bundleClass = createString("bundleClass");

    public final SimplePath<com.fbi.engine.domain.schema.ConnectionPropertiesSchema> connectionPropertiesSchema = createSimple("connectionPropertiesSchema", com.fbi.engine.domain.schema.ConnectionPropertiesSchema.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public QConnectionType(String variable) {
        super(ConnectionType.class, forVariable(variable));
    }

    public QConnectionType(Path<? extends ConnectionType> path) {
        super(path.getType(), path.getMetadata());
    }

    public QConnectionType(PathMetadata metadata) {
        super(ConnectionType.class, metadata);
    }

}

