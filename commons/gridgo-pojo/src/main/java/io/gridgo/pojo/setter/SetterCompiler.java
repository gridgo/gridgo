package io.gridgo.pojo.setter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface SetterCompiler {

    PojoSetter compile(Method method);

    PojoSetter compile(Field field);
}
