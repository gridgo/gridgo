package io.gridgo.pojo.getter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface GetterCompiler {

    PojoGetter compile(Method method);

    PojoGetter compile(Field field);
}
