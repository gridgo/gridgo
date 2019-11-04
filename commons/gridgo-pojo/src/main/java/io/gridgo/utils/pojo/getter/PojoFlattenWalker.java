package io.gridgo.utils.pojo.getter;

import io.gridgo.utils.pojo.PojoFlattenIndicator;

public interface PojoFlattenWalker {

    void accept(PojoFlattenIndicator indicator, Object value);
}
