package io.gridgo.utils.pojo.setter;

import static io.gridgo.utils.pojo.PojoUtils.extractSetterMethodSignatures;

import java.util.List;

import io.gridgo.utils.pojo.PojoMethodSignature;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

class JavassistSetterProxyBuilder implements PojoSetterProxyBuilder {

    @Override
    @SuppressWarnings("unchecked")
    public PojoSetterProxy buildSetterProxy(Class<?> target) {
        try {
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(target));

            String className = target.getName().replaceAll("\\.", "_") + "_setter_proxy_" + System.nanoTime();
            CtClass cc = pool.makeClass(className);

            cc.defrost();
            cc.addInterface(pool.get(PojoSetterProxy.class.getName()));

            List<PojoMethodSignature> methodSignatures = extractSetterMethodSignatures(target);
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
                } else if (methodSignature.getFieldType().isArray()) {
                    invoked += "((" + methodSignature.getComponentType().getName() + "[]) value);";
                } else {
                    invoked += "((" + methodSignature.getFieldType().getName() + ") value);";
                }

                method += "\tif (\"" + fieldName + "\".equals(fieldName)) " + invoked + "\n"; //
            }
            method += "\n}";

            cc.addMethod(CtMethod.make(method, cc));

            return (PojoSetterProxy) cc.toClass().getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
