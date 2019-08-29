package io.gridgo.utils.pojo.translator;

@FunctionalInterface
public interface ValueTranslator {

    Object translate(Object obj);
}
