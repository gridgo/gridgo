package io.gridgo.pojo.reflect;

import static java.util.Collections.unmodifiableMap;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

public interface PojoReflectiveStruct {

    Class<?> type();

    Map<String, PojoReflectiveGetter> getters();

    Map<String, List<PojoReflectiveSetter>> setters();

    default PojoReflectiveGetter findGetter(String fieldName) {
        return getters().get(fieldName);
    }

    default List<PojoReflectiveSetter> findSetters(String fieldName) {
        return setters().get(fieldName);
    }

    static PojoReflectiveStruct of(Class<?> type) {
        var pair = PojoReflectiveAccessors.extract(type);
        return new SimplePojoReflectiveStructure(type, pair.getLeft(), pair.getRight());
    }
}

@Getter
@Accessors(fluent = true)
class SimplePojoReflectiveStructure implements PojoReflectiveStruct {

    private final @NonNull Class<?> type;

    private final @NonNull Map<String, PojoReflectiveGetter> getters;

    private final @NonNull Map<String, List<PojoReflectiveSetter>> setters;

    SimplePojoReflectiveStructure(Class<?> type, List<PojoReflectiveGetter> getters,
            List<PojoReflectiveSetter> setters) {
        this.type = type;
        this.getters = unmodifiableMap(initGetterMap(getters));
        this.setters = unmodifiableMap(initSetterMap(setters));
    }

    private Map<String, List<PojoReflectiveSetter>> initSetterMap(@NonNull List<PojoReflectiveSetter> setters) {
        var map = new HashMap<String, List<PojoReflectiveSetter>>();
        for (var setter : setters)
            map.computeIfAbsent(setter.fieldName(), k -> new LinkedList<>()).add(setter);
        return map;
    }

    private Map<String, PojoReflectiveGetter> initGetterMap(@NonNull List<PojoReflectiveGetter> getters) {
        var map = new HashMap<String, PojoReflectiveGetter>();
        for (var getter : getters) {
            var old = map.putIfAbsent(getter.fieldName(), getter);
            if (old != null && (getter.element().isField() && old.element().isMethod()))
                map.put(getter.fieldName(), getter); // override
        }
        return map;
    }
}
