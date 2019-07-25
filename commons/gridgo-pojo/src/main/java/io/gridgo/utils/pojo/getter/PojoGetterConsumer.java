package io.gridgo.utils.pojo.getter;

import io.gridgo.utils.pojo.PojoMethodSignature;

public interface PojoGetterConsumer {

    void accept(PojoMethodSignature signature, Object value);
}
