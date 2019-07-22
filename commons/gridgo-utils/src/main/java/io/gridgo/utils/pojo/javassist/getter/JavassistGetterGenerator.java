package io.gridgo.utils.pojo.javassist.getter;

import java.util.UUID;

import io.gridgo.utils.pojo.PojoMethodSignature;
import io.gridgo.utils.pojo.getter.PojoGetter;
import io.gridgo.utils.pojo.getter.PojoGetterGenerator;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class JavassistGetterGenerator implements PojoGetterGenerator {

    @Override
    @SuppressWarnings("unchecked")
    public PojoGetter generateGetter(Class<?> type, PojoMethodSignature signature) {
        try {
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(type));

            CtClass cc = pool.makeClass(UUID.randomUUID().toString());
            CtClass implementedInterface = pool.get(PojoGetter.class.getName());
            cc.addInterface(implementedInterface);

            StringBuffer method = new StringBuffer() //
                    .append("public Object get(Object target) {") //
                    .append("return ((").append(type.getName()).append(") target).").append(signature.getMethodName())
                    .append("();") //
                    .append("}");

            cc.addMethod(CtMethod.make(method.toString(), cc));
            return (PojoGetter) cc.toClass().getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
