package io.gridgo.pojo.reflect;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

public interface PojoReflectiveStruct {

    Class<?> type();

    List<PojoReflectiveGetter> getters();

    List<PojoReflectiveSetter> setters();

    static PojoReflectiveStruct of(Class<?> type) {
        return PojoReflectiveStructs.build(type);
    }
}

@Getter
@Accessors(fluent = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class SimplePojoReflectiveStructure implements PojoReflectiveStruct {

    private final @NonNull Class<?> type;

    private final @NonNull List<PojoReflectiveGetter> getters;

    private final @NonNull List<PojoReflectiveSetter> setters;
}

class PojoReflectiveStructs {

    private static final Map<Class<?>, PojoReflectiveStruct> cache = new ConcurrentHashMap<>();

    static PojoReflectiveStruct build(Class<?> type) {
        return cache.computeIfAbsent(type, PojoReflectiveStructs::doBuild);
    }

    private static PojoReflectiveStruct doBuild(Class<?> type) {
        var pair = PojoReflectiveAccessors.extract(type);
        return new SimplePojoReflectiveStructure(type, pair.getLeft(), pair.getRight());
    }
}