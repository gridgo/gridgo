package io.gridgo.pojo.reflect;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

public interface PojoReflectiveStruct {

    Class<?> type();

    List<PojoReflectiveGetter> getters();

    List<PojoReflectiveSetter> setters();

    static PojoReflectiveStruct of(Class<?> type) {
        var pair = PojoReflectiveAccessors.extract(type);
        return new SimplePojoReflectiveStructure(type, pair.getLeft(), pair.getRight());
    }
}

@Getter
@Accessors(fluent = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class SimplePojoReflectiveStructure implements PojoReflectiveStruct {

    private final Class<?> type;

    private final List<PojoReflectiveGetter> getters;

    private final List<PojoReflectiveSetter> setters;
}
