package io.gridgo.pojo.getter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DefaultGetterCompiler extends AbstractGetterCompiler {

    @Getter
    private static final DefaultGetterCompiler instance = new DefaultGetterCompiler();

    @Override
    public PojoGetter compile(Method method) {
        var classSrc = buildImplClass(method, PojoGetter.class, "get");
        return doCompile(classSrc.getName(), classSrc.toString());
    }

    @Override
    public PojoGetter compile(Field field) {

        return null;
    }
}
