package io.gridgo.utils.pojo.getter;

import lombok.NonNull;

public interface PojoGetterRegistry {

    static PojoGetterRegistry DEFAULT = PojoGetterRegistryImpl.getInstance();

    PojoGetterProxy getGetterProxy(@NonNull Class<?> type);
}
