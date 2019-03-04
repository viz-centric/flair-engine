package com.fbi.engine.domain;

import com.fbi.engine.config.types.JsonBinaryType;
import com.fbi.engine.config.types.JsonStringType;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.MappedSuperclass;

@TypeDefs({
    @TypeDef(name = "json", typeClass = JsonStringType.class),
    @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
@MappedSuperclass
class BaseEntity {
}
