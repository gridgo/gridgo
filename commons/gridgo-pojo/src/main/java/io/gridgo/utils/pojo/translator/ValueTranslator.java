package io.gridgo.utils.pojo.translator;

public interface ValueTranslator<From, To> {

    To translate(From obj);

    default boolean canApply(Object obj) {
        return true;
    }
}
