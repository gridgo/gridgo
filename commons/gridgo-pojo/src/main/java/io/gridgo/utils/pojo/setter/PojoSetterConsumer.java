package io.gridgo.utils.pojo.setter;

import io.gridgo.utils.pojo.PojoFieldSignature;

public interface PojoSetterConsumer {

    Object apply(PojoFieldSignature signature);
}
