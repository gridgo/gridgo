package io.gridgo.pojo.translator;

public interface PojoUnboxer {

    Class<?> targetType();

    Class<?> returnType();

    String methodName();

    Class<?> declaringClass();
}
