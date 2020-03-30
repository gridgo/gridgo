package io.gridgo.utils.pojo.getter;

import io.gridgo.utils.pojo.PojoFlattenIndicator;
import io.gridgo.utils.pojo.PojoFieldSignature;

@FunctionalInterface
public interface PojoFlattenAcceptor {

    void accept(PojoFlattenIndicator indicator, Object value, PojoFieldSignature signature, PojoGetterProxy proxy);
}
