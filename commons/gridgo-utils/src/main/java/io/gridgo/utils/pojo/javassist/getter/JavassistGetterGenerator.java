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

            String methodName = signature.getMethodName();
            String targetType = type.getName();
            String castedTarget = "((" + targetType + ") target)";
            var method = new StringBuilder() //
                    .append("public Object get(Object target) {\n");

            Class<?> fieldType = signature.getFieldType();
            if (fieldType.isPrimitive()) {
                Class<?> wrapperType = signature.getWrapperType();
                method.append("\treturn ") //
                        .append(wrapperType.getName()) //
                        .append(".valueOf(") //
                        .append(castedTarget) //
                        .append(".") //
                        .append(methodName) //
                        .append("());");
            } else {
                method.append("\treturn ") //
                        .append(castedTarget) //
                        .append(".") //
                        .append(methodName) //
                        .append("();");
            }
            method.append("\n}");

            cc.addMethod(CtMethod.make(method.toString(), cc));
            return (PojoGetter) cc.toClass().getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
