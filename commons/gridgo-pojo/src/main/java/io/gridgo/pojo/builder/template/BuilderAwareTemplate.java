package io.gridgo.pojo.builder.template;

import io.gridgo.pojo.builder.PojoSchemaBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Delegate;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public abstract class BuilderAwareTemplate<T> extends PojoSchemaTemplate<T> {

    @Delegate
    @Getter(value = AccessLevel.PROTECTED)
    private final @NonNull PojoSchemaBuilder<?> builder;

}
