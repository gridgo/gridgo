package io.gridgo.utils.pojo.getter;

import io.gridgo.utils.pojo.PojoMethodSignature;
import io.gridgo.utils.pojo.asm.getter.AsmGetterGenerator;
import io.gridgo.utils.pojo.getter.javassist.JavassistGetterGenerator;

public interface PojoGetterGenerator {

    PojoGetter generateGetter(Class<?> type, PojoMethodSignature methodSignature);

    static PojoGetterGenerator newJavassist() {
        return new JavassistGetterGenerator();
    }

    static PojoGetterGenerator newAsm() {
        return new AsmGetterGenerator();
    }
}
