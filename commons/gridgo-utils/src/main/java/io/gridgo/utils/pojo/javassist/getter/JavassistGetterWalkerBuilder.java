package io.gridgo.utils.pojo.javassist.getter;

import java.util.List;
import java.util.UUID;

import io.gridgo.utils.pojo.PojoMethodSignature;
import io.gridgo.utils.pojo.getter.PojoGetterSignatures;
import io.gridgo.utils.pojo.getter.PojoGetterWalker;
import io.gridgo.utils.pojo.getter.PojoGetterWalkerBuilder;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class JavassistGetterWalkerBuilder implements PojoGetterWalkerBuilder {

    @Override
    @SuppressWarnings("unchecked")
    public PojoGetterWalker buildGetterWalker(Class<?> target) {
        try {
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(target));

            CtClass cc = pool.makeClass(UUID.randomUUID().toString());
            CtClass implementedInterface = pool.get(PojoGetterWalker.class.getName());

            cc.addInterface(implementedInterface);

            List<PojoMethodSignature> methodSignatures = PojoGetterSignatures.extractMethodSignatures(target);
            StringBuilder allFields = new StringBuilder();
            for (PojoMethodSignature signature : methodSignatures) {
                if (allFields.length() > 0) {
                    allFields.append(",");
                }
                allFields.append("\"").append(signature.getFieldName()).append("\"");
            }
            String castedTarget = "((" + target.getName() + ") target)";
            String method = "public void walkThroughFields(Object target, io.gridgo.utils.pojo.getter.PojoGetterWalkerConsumer valueConsumer, String[] fields) { \n" //
                    + "\tif (fields == null || fields.length == 0) fields = new String[] {" + allFields + "};\n" //
                    + "\t" + target.getName() + " castedTarget = " + castedTarget + ";\n" //
                    + "\tfor (int i=0; i<fields.length; i++) { \n" //
                    + "\t\tString field = fields[i];\n"; //

            for (PojoMethodSignature methodSignature : methodSignatures) {
                String fieldName = methodSignature.getFieldName();
                String invoked = "castedTarget." + methodSignature.getMethodName() + "()";
                if (methodSignature.getFieldType().isPrimitive()) {
                    invoked = methodSignature.getWrapperType().getName() + ".valueOf(" + invoked + ")";
                }
                method += "\t\tif (\"" + fieldName + "\".equals(field))\n" //
                        + "\t\t\tvalueConsumer.accept(\"" + fieldName + "\", " + invoked + "); \n"; //
            }
            method += "\t}\n}";

            cc.addMethod(CtMethod.make(method, cc));

            return (PojoGetterWalker) cc.toClass().getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
