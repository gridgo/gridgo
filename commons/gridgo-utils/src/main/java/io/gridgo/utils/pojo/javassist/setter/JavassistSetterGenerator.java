package io.gridgo.utils.pojo.javassist.setter;

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

            String className = type.getName().replaceAll("\\.", "_") + "_" + signature.getMethodName() + "_invoker";
            CtClass cc = pool.makeClass(className);

            CtClass implementedInterface = pool.get(PojoSetter.class.getName());
            cc.addInterface(implementedInterface);

            var method = new StringBuilder() //
                    .append("public void set(Object target, Object value) { \n") //
                    .append("\t((").append(type.getName()).append(") target).").append(signature.getMethodName())
                    .append("(");

            Class<?> fieldType = signature.getFieldType();
            if (fieldType.isPrimitive()) {
                Class<?> wrapperType = signature.getWrapperType();
                method.append("((" + wrapperType.getTypeName() + ") value)." + fieldType.getTypeName() + "Value()");
            } else {
                method.append("(" + fieldType.getTypeName() + ") value");
            }

            method.append(");\n}");

            cc.addMethod(CtMethod.make(method.toString(), cc));
            return (PojoSetter) cc.toClass().getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(
                    "error while building setter, target: " + type.getName() + ", field: " + signature.getFieldName(),
                    e);
        }
    }
}
