package io.gridgo.utils.pojo.translator;

import io.gridgo.utils.pojo.PojoMethodSignature;

public interface ValueTranslator<From, To> {

    To translate(From obj, PojoMethodSignature signature);

    default boolean translatable(Object obj) {
        return true;
    }
}
