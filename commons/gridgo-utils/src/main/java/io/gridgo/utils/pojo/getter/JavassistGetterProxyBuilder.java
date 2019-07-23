package io.gridgo.utils.pojo.getter;

import static io.gridgo.utils.pojo.PojoUtils.extractGetterMethodSignatures;

import java.util.List;

import io.gridgo.utils.pojo.PojoMethodSignature;
import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;

class JavassistGetterProxyBuilder implements PojoGetterProxyBuilder {

    @Override
    @SuppressWarnings("unchecked")
    public PojoGetterProxy buildGetterWalker(Class<?> target) {
        String className = target.getName().replaceAll("\\.", "_") + "_getter_proxy_" + System.nanoTime();
        try {
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(target));

            CtClass cc = pool.makeClass(className);

            cc.defrost();
            cc.addInterface(pool.get(PojoGetterProxy.class.getName()));

            List<PojoMethodSignature> methodSignatures = extractGetterMethodSignatures(target);
            StringBuilder allFieldsBuilder = new StringBuilder();
            for (PojoMethodSignature signature : methodSignatures) {
                if (allFieldsBuilder.length() > 0) {
                    allFieldsBuilder.append(",");
                }
                allFieldsBuilder.append("\"").append(signature.getFieldName()).append("\"");
            }

            String typeName = target.getName();
            String allFields = allFieldsBuilder.toString();

            buildGetFieldsMethod(cc, allFields);

            buildGetValueMethod(cc, typeName, methodSignatures);

            buildWalkThroughMethod(cc, typeName, methodSignatures, allFields);

            return (PojoGetterProxy) cc.toClass().getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("error while trying to build getter proxy: " + className, e);
        }
    }

    private void buildGetValueMethod(CtClass cc, String typeName, List<PojoMethodSignature> methodSignatures)
            throws CannotCompileException {
        String castedTarget = "((" + typeName + ") target)";
        String method = "public Object getValue(Object target, String fieldName) { \n" //
                + "\t" + typeName + " castedTarget = " + castedTarget + ";\n"; //

        for (PojoMethodSignature methodSignature : methodSignatures) {
            String fieldName = methodSignature.getFieldName();
            String invoked = "castedTarget." + methodSignature.getMethodName() + "()";
            if (methodSignature.getFieldType().isPrimitive()) {
                invoked = methodSignature.getWrapperType().getName() + ".valueOf(" + invoked + ")";
            }
            method += "\tif (\"" + fieldName + "\".equals(fieldName))\n" //
                    + "\t\treturn " + invoked + "; \n"; //
        }
        method += "\treturn null;\n";
        method += "}";

        cc.addMethod(CtMethod.make(method, cc));
    }

    private void buildWalkThroughMethod(CtClass cc, String typeName, List<PojoMethodSignature> methodSignatures,
            String allFields) throws CannotCompileException {

        String castedTarget = "((" + typeName + ") target)";
        String method = "public void walkThrough(Object target, io.gridgo.utils.pojo.getter.PojoGetterConsumer valueConsumer, String[] fields) { \n" //
                + "\tif (fields == null || fields.length == 0) fields = new String[] {" + allFields + "};\n" //
                + "\t" + typeName + " castedTarget = " + castedTarget + ";\n" //
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
    }

    private void buildGetFieldsMethod(CtClass cc, String allFields) throws CannotCompileException {
        CtField field = CtField.make("private String[] fields = new String[] {" + allFields + "};", cc);
        cc.addField(field);

        String method = "public String[] getFields() { return this.fields; }";
        cc.addMethod(CtMethod.make(method, cc));
    }
}
