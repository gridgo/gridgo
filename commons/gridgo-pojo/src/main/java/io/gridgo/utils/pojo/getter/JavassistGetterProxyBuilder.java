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
import lombok.NonNull;

class JavassistGetterProxyBuilder implements PojoGetterProxyBuilder {

    @Override
    public PojoGetterProxy buildGetterProxy(@NonNull Class<?> target) {
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

            buildGetSignaturesMethod(cc);
            buildGetFieldsMethod(cc, allFields);
            buildSetSignatureMethod(cc, methodSignatures);
            buildGetValueMethod(cc, typeName, methodSignatures);
            buildWalkThroughAllMethod(cc, typeName, methodSignatures);
            buildWalkThroughMethod(cc, typeName, methodSignatures, allFields);

            Class<?> resultClass = cc.toClass();
            PojoGetterProxy result = (PojoGetterProxy) resultClass.getConstructor().newInstance();
            var signatureSetter = resultClass.getMethod("setMethodSignature", String.class, PojoMethodSignature.class);
            for (PojoMethodSignature signature : methodSignatures) {
                signatureSetter.invoke(result, signature.getFieldName(), signature);
            }

            return result;
        } catch (Exception e) {
            throw new RuntimeException("error while trying to build getter proxy: " + target, e);
        }
    }

    private void buildGetSignaturesMethod(CtClass cc) throws CannotCompileException {
        CtField field = CtField.make("private java.util.List signatures = new java.util.ArrayList();", cc);
        cc.addField(field);

        String method = "public java.util.List getSignatures() { return this.signatures; }";
        cc.addMethod(CtMethod.make(method, cc));
    }

    private void buildSetSignatureMethod(CtClass cc, List<PojoMethodSignature> methodSignatures)
            throws CannotCompileException {
        String type = "io.gridgo.utils.pojo.PojoMethodSignature";
        String subfix = "Signature";
        for (PojoMethodSignature methodSignature : methodSignatures) {
            String fieldName = methodSignature.getFieldName() + subfix;
            cc.addField(CtField.make("private " + type + " " + fieldName + ";", cc));
        }

        String method = "public void setMethodSignature(String fieldName, " + type + " value) {\n";
        method += "\tfor (int i=0; i<this.fields.length; i++) {"; // start for loop via all field
        for (PojoMethodSignature methodSignature : methodSignatures) {
            String fieldName = methodSignature.getFieldName();
            String signFieldName = fieldName + subfix;
            method += "\t\tif (\"" + fieldName + "\".equals(fieldName)) {\n";
            method += "\t\t\t" + signFieldName + " = value;\n";
            method += "\t\t\tthis.signatures.add(value); \n";
            method += "\t\t}\n";
        }
        method += "\t}\n"; // end of for
        method += "}"; // end of method

        cc.addMethod(CtMethod.make(method, cc));
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

    private void buildGetFieldsMethod(CtClass cc, String allFields) throws CannotCompileException {
        String initValue = allFields.length() == 0 ? "new String[0];" : "new String[] {" + allFields + "};";
        CtField field = CtField.make("private String[] fields = " + initValue, cc);
        cc.addField(field);

        String method = "public String[] getFields() { return this.fields; }";
        cc.addMethod(CtMethod.make(method, cc));
    }

    private void buildWalkThroughMethod(CtClass cc, String typeName, List<PojoMethodSignature> methodSignatures,
            String allFields) throws CannotCompileException {

        String signatureFieldSubfix = "Signature";

        String method = "public void walkThrough(Object target, io.gridgo.utils.pojo.getter.PojoGetterConsumer consumer, String[] fields) { \n";
        method += "    if (fields == null || fields.length == 0) this.walkThroughAll(target, consumer); return;\n";
        method += "    " + typeName + " castedTarget = (" + typeName + ") target;\n";
        method += "    for (int i=0; i<fields.length; i++) { \n";
        method += "        String field = fields[i];\n";

        for (PojoMethodSignature methodSignature : methodSignatures) {
            String fieldName = methodSignature.getFieldName();
            String invokeGetter = "castedTarget." + methodSignature.getMethodName() + "()";
            if (methodSignature.getFieldType().isPrimitive()) {
                invokeGetter = methodSignature.getWrapperType().getName() + ".valueOf(" + invokeGetter + ")";
            }
            String signatureField = fieldName + signatureFieldSubfix;
            method += "        if (\"" + fieldName + "\".equals(field))\n";
            method += "            consumer.accept(this." + signatureField + ", " + invokeGetter + "); \n"; //
        }
        method += "    }\n";
        method += "}";

        cc.addMethod(CtMethod.make(method, cc));
    }

    private void buildWalkThroughAllMethod(CtClass cc, String typeName, List<PojoMethodSignature> methodSignatures)
            throws CannotCompileException {

        String signatureFieldSubfix = "Signature";

        String method = "private void walkThroughAll(Object target, io.gridgo.utils.pojo.getter.PojoGetterConsumer consumer) { \n";
        method += "    " + typeName + " castedTarget = (" + typeName + ") target;\n";

        for (PojoMethodSignature methodSignature : methodSignatures) {
            String fieldName = methodSignature.getFieldName();
            String invoked = "castedTarget." + methodSignature.getMethodName() + "()";
            if (methodSignature.getFieldType().isPrimitive()) {
                invoked = methodSignature.getWrapperType().getName() + ".valueOf(" + invoked + ")";
            }
            String signatureField = fieldName + signatureFieldSubfix;
            method += "    consumer.accept(this." + signatureField + ", " + invoked + "); \n"; //
        }
        method += "}";

        cc.addMethod(CtMethod.make(method, cc));
    }
}
