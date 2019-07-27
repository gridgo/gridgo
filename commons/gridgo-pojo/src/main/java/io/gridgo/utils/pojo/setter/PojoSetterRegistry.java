package io.gridgo.utils.pojo.setter;

import lombok.NonNull;

public interface PojoSetterRegistry {

    static PojoSetterRegistry DEFAULT = PojoSetterRegistryImpl.getInstance();

    PojoSetterProxy getSetterProxy(@NonNull Class<?> type);
}
