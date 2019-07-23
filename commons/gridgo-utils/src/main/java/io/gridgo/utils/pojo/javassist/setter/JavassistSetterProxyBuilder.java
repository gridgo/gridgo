package io.gridgo.utils.pojo.javassist.setter;

import java.util.List;
import java.util.UUID;

import io.gridgo.utils.pojo.PojoMethodSignature;
import io.gridgo.utils.pojo.setter.PojoSetterProxy;
import io.gridgo.utils.pojo.setter.PojoSetterProxyBuilder;
import io.gridgo.utils.pojo.setter.PojoSetterSignatures;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class JavassistSetterProxyBuilder implements PojoSetterProxyBuilder {

    @Override
    @SuppressWarnings("unchecked")
    public PojoSetterProxy buildSetterProxy(Class<?> target) {
        try {
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(target));

            CtClass cc = pool.makeClass(UUID.randomUUID().toString());
            CtClass implementedInterface = pool.get(PojoSetterProxy.class.getName());

            cc.addInterface(implementedInterface);

            List<PojoMethodSignature> methodSignatures = PojoSetterSignatures.extractMethodSignatures(target);
            StringBuilder allFields = new StringBuilder();
            for (PojoMethodSignature signature : methodSignatures) {
                if (allFields.length() > 0) {
                    allFields.append(",");
                }
                allFields.append("\"").append(signature.getFieldName()).append("\"");
            }
            String castedTarget = "((" + target.getName() + ") target)";
            String method = "public void applyValue(Object target, String fieldName, Object value) { \n"; //
            method += "\t" + target.getName() + " castedTarget = " + castedTarget + ";\n";
            for (PojoMethodSignature methodSignature : methodSignatures) {
                String fieldName = methodSignature.getFieldName();
                String invoked = "castedTarget." + methodSignature.getMethodName();
                if (methodSignature.getFieldType().isPrimitive()) {
                    invoked += "(((" + methodSignature.getWrapperType().getName() + ") value)."
                            + methodSignature.getFieldType().getTypeName() + "Value());";
                } else {
                    invoked += "((" + methodSignature.getFieldType().getName() + ") value);";
                }
                method += "\tif (\"" + fieldName + "\".equals(fieldName)) " + invoked + "\n"; //
            }
            method += "\n}";

            cc.addMethod(CtMethod.make(method, cc));

            return (PojoSetterProxy) cc.toClass().getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
