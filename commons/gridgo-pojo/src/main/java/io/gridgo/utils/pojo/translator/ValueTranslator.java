package io.gridgo.utils.pojo.translator;

@FunctionalInterface
public interface ValueTranslator<From, To> {

    To translate(From obj);
}
