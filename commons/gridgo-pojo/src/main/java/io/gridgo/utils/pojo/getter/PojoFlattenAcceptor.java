package io.gridgo.utils.pojo.getter;

import io.gridgo.utils.pojo.PojoFlattenIndicator;
import io.gridgo.utils.pojo.PojoMethodSignature;

public interface PojoFlattenAcceptor {

    void accept(PojoFlattenIndicator indicator, Object value, PojoMethodSignature signature);
}
