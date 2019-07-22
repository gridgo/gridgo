package io.gridgo.utils.pojo.javassist.setter;

import java.util.UUID;

import io.gridgo.utils.pojo.PojoMethodSignature;
import io.gridgo.utils.pojo.setter.PojoSetter;
import io.gridgo.utils.pojo.setter.PojoSetterGenerator;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class JavassistSetterGenerator implements PojoSetterGenerator {

    @Override
    @SuppressWarnings("unchecked")
    public PojoSetter generateSetter(Class<?> type, PojoMethodSignature signature) {
        try {
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(type));

            CtClass cc = pool.makeClass(UUID.randomUUID().toString());
            CtClass implementedInterface = pool.get(PojoSetter.class.getName());
            cc.addInterface(implementedInterface);

            StringBuffer method = new StringBuffer() //
                    .append("public void set(Object target, Object value) {") //
                    .append("((").append(type.getName()).append(") target).").append(signature.getMethodName()) //
                    .append("((").append(signature.getFieldType().getName()).append(") value);") //
                    .append("}");

            cc.addMethod(CtMethod.make(method.toString(), cc));
            return (PojoSetter) cc.toClass().getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
