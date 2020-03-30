package io.gridgo.utils.pojo.getter;

import io.gridgo.utils.pojo.PojoFieldSignature;

public interface PojoGetterConsumer {

    void accept(PojoFieldSignature signature, Object value);
}
