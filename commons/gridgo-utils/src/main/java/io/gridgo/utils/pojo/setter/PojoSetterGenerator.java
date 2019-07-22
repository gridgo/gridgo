package io.gridgo.utils.pojo.setter;

import io.gridgo.utils.pojo.PojoMethodSignature;
import io.gridgo.utils.pojo.asm.setter.AsmSetterGenerator;

public interface PojoSetterGenerator {

    PojoSetter generateSetter(Class<?> type, PojoMethodSignature signature);

    static PojoSetterGenerator newJavassist() {
        return new JavassistSetterGenerator();
    }

    static PojoSetterGenerator newAsm() {
        return new AsmSetterGenerator();
    }
}
