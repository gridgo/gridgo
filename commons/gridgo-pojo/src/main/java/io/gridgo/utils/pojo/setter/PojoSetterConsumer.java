package io.gridgo.utils.pojo.setter;

import io.gridgo.utils.pojo.PojoMethodSignature;

public interface PojoSetterConsumer {

    Object apply(PojoMethodSignature signature);
}
