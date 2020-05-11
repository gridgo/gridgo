package io.gridgo.pojo.builder.template;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter(value = AccessLevel.PROTECTED)
public abstract class PojoSchemaTemplate<T> {

    public abstract T apply();
}
