package io.gridgo.pojo.field;

@FunctionalInterface
public interface PojoInstrumentor<From, To> {

    To convert(From from);
}
