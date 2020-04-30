package io.gridgo.pojo.reflect;

import io.gridgo.pojo.support.PojoAccessorType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

public interface PojoReflectiveAccessor {

    String name();

    PojoReflectiveElement element();

    PojoAccessorType type();

    default boolean isSetter() {
        return type() == PojoAccessorType.SET;
    }

    default boolean isGetter() {
        return type() == PojoAccessorType.GET;
    }
}

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class AbstractPojoReflectiveAccessor implements PojoReflectiveAccessor {

    private final @NonNull PojoAccessorType type;

    private final @NonNull String name;

    private final @NonNull PojoReflectiveElement element;

}