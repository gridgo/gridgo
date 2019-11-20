package io.gridgo.utils.pojo.translator;

public interface ValueTranslator<From, To> {

    To translate(From obj);

    default boolean translatable(Object obj) {
        return true;
    }
}
