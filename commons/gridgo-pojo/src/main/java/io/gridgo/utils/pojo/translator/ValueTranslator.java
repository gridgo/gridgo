package io.gridgo.utils.pojo.translator;

import io.gridgo.utils.pojo.PojoFieldSignature;

public interface ValueTranslator<From, To> {

    To translate(From obj, PojoFieldSignature signature);

    default boolean translatable(Object obj) {
        return true;
    }
}
