package io.gridgo.pojo.setter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DefaultSetterCompiler extends AbstractSetterCompiler {

    @Getter
    private static final DefaultSetterCompiler instance = new DefaultSetterCompiler();

    @Override
    public PojoSetter compile(Method method) {
        var classSrc = buildImplClass(method, PojoSetter.class, "set");
        return doCompile(classSrc.getName(), classSrc.toString());
    }

    @Override
    public PojoSetter compile(Field field) {
        var classSrc = buildImplClass(field, PojoSetter.class, "set");
        return doCompile(classSrc.getName(), classSrc.toString());
    }
}
