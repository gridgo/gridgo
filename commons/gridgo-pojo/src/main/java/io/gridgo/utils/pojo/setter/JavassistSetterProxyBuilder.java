package io.gridgo.utils.pojo.setter;

import static io.gridgo.utils.pojo.PojoUtils.extractSetterMethodSignatures;

import java.util.List;

import io.gridgo.utils.PrimitiveUtils;
import io.gridgo.utils.pojo.PojoMethodSignature;
import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;

class JavassistSetterProxyBuilder implements PojoSetterProxyBuilder {

    @Override
    public PojoSetterProxy buildSetterProxy(Class<?> target) {
        try {
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(target));

            String className = target.getName().replaceAll("\\.", "_") + "_setter_proxy_" + System.nanoTime();
            CtClass cc = pool.makeClass(className);

            cc.defrost();
            cc.addInterface(pool.get(PojoSetterProxy.class.getName()));

            List<PojoMethodSignature> methodSignatures = extractSetterMethodSignatures(target);

            StringBuilder allFieldsBuilder = new StringBuilder();
            for (PojoMethodSignature methodSignature : methodSignatures) {
                if (allFieldsBuilder.length() > 0) {
                    allFieldsBuilder.append(",");
                }
                allFieldsBuilder.append('"').append(methodSignature.getFieldName()).append('"');
            }
            String allFields = allFieldsBuilder.toString();

            String targetType = target.getName();

            buildGetFieldsMethod(cc, allFields);
            buildSetSignatureMethod(cc, methodSignatures);
            buildApplyValueMethod(cc, methodSignatures, targetType);
            buildWalkthroughAllMethod(cc, methodSignatures, targetType);
            buildWalkthroughMethod(cc, methodSignatures, targetType, allFields);

            Class<?> resultClass = cc.toClass();
            var result = (PojoSetterProxy) resultClass.getConstructor().newInstance();
            var signatureSetter = resultClass.getMethod("setMethodSignature", String.class, PojoMethodSignature.class);
            for (PojoMethodSignature methodSignature : methodSignatures) {
                signatureSetter.invoke(result, methodSignature.getFieldName(), methodSignature);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
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
            method += "\t\tif (\"" + fieldName + "\".equals(fieldName)) " + signFieldName + " = value;\n";
        }
        method += "\t}\n"; // end of for
        method += "}"; // end of method

        cc.addMethod(CtMethod.make(method, cc));
    }

    private void buildGetFieldsMethod(CtClass cc, String allFields) throws CannotCompileException {
        String initValue = allFields.length() == 0 ? "new String[0];" : "new String[] {" + allFields + "};";
        CtField field = CtField.make("private String[] fields = " + initValue, cc);
        cc.addField(field);

        String method = "public String[] getFields() { return this.fields; }";
        cc.addMethod(CtMethod.make(method, cc));
    }

    private void buildApplyValueMethod(CtClass cc, List<PojoMethodSignature> methodSignatures, String targetType)
            throws CannotCompileException {
        String castedTarget = "((" + targetType + ") target)";
        String method = "public void applyValue(Object target, String fieldName, Object value) { \n"; //
        method += "\t" + targetType + " castedTarget = " + castedTarget + ";\n";
        for (PojoMethodSignature methodSignature : methodSignatures) {
            String fieldName = methodSignature.getFieldName();
            String invokeSetter = buildInvokeSetter(methodSignature);
            method += "\tif (\"" + fieldName + "\".equals(fieldName)) " + invokeSetter + ";\n"; //
        }
        method += "\n}";

        cc.addMethod(CtMethod.make(method, cc));
    }

    private String buildInvokeSetter(PojoMethodSignature methodSignature) {
        String invokeSetter = "castedTarget." + methodSignature.getMethodName();
        Class<?> fieldType = methodSignature.getFieldType();
        if (methodSignature.isPrimitiveOrWrapperType()) {
            String wrapperTypeName = methodSignature.getWrapperType().getName();
            if (PrimitiveUtils.isNumberClass(fieldType)) { // if method receive number
                String numberType = Number.class.getName();
                if (methodSignature.isPrimitiveType()) { // receive primitive number
                    invokeSetter += "(((" + numberType + ") value)." + fieldType.getTypeName() + "Value())";
                } else { // receive wrapper type
                    var primitiveTypeName = methodSignature.getPrimitiveTypeFromWrapperType().getName();
                    invokeSetter += "(" + wrapperTypeName + ".valueOf(((" + numberType + ") value)." + primitiveTypeName
                            + "Value()))";
                }
            } else if (fieldType.isPrimitive()) {
                invokeSetter += "(((" + wrapperTypeName + ") value)." + fieldType.getName() + "Value())";
            } else {
                invokeSetter += "((" + wrapperTypeName + ") value)";
            }
        } else if (fieldType.isArray()) {
            invokeSetter += "((" + methodSignature.getComponentType().getName() + "[]) value)";
        } else {
            invokeSetter += "((" + fieldType.getName() + ") value)";
        }
        return invokeSetter;
    }

    private void buildWalkthroughMethod(CtClass cc, List<PojoMethodSignature> methodSignatures, String targetType,
            String allFields) throws CannotCompileException {

        String signatureFieldSubfix = "Signature";
        String holderType = ValueHolder.class.getName();

        String castedTarget = "(" + targetType + ") target";

        String method = "public void walkThrough(Object target, io.gridgo.utils.pojo.setter.PojoSetterConsumer consumer, String[] fields) { \n"; //
        method += "    if (fields == null || fields.length == 0) this.walkThroughAll(target, consumer); return;\n";
        method += "    " + targetType + " castedTarget = " + castedTarget + ";\n";
        method += "    for (int i=0; i < fields.length; i++) {\n"; // start for loop via fields
        method += "        String fieldName = fields[i];\n"; // create temp variable `fieldName`

        for (PojoMethodSignature methodSignature : methodSignatures) {
            String fieldName = methodSignature.getFieldName();
            String invokeSetter = buildInvokeSetter(methodSignature);
            String signatureFieldName = fieldName + signatureFieldSubfix;
            method += "        if (fieldName.equals(\"" + fieldName + "\")) {\n"; // start if 1
            method += "            Object value = consumer.apply(this." + signatureFieldName + ");\n";
            method += "            if (!(value instanceof " + holderType + ")) {\n"; // start if 2
            method += "                " + invokeSetter + ";\n";
            method += "            } else { \n"; // else if 2
            method += "                " + holderType + " holder = (" + holderType + ") value; \n";
            method += "                if (holder.hasValue()) {\n"; // start if 4
            method += "                    value = holder.getValue();\n";
            method += "                    " + invokeSetter + ";\n";
            method += "                }\n"; // end if 4
            method += "            }\n"; // end if 2
            method += "        }\n"; // end if 1
        }

        method += "    }"; // end of for
        method += "\n}"; // end of method

        cc.addMethod(CtMethod.make(method, cc));
    }

    private void buildWalkthroughAllMethod(CtClass cc, List<PojoMethodSignature> methodSignatures, String targetType)
            throws CannotCompileException {

        String signatureFieldSubfix = "Signature";
        String holderType = ValueHolder.class.getName();

        String castedTarget = "(" + targetType + ") target";

        String method = "private void walkThroughAll(Object target, io.gridgo.utils.pojo.setter.PojoSetterConsumer consumer) { \n"; //
        method += "    " + targetType + " castedTarget = " + castedTarget + ";\n";
        method += "    Object value = null;\n";

        for (PojoMethodSignature methodSignature : methodSignatures) {
            String fieldName = methodSignature.getFieldName();

            String invokeSetter = buildInvokeSetter(methodSignature);

            String signatureFieldName = fieldName + signatureFieldSubfix;
            method += "    value = consumer.apply(this." + signatureFieldName + ");\n";
            method += "    if (!(value instanceof " + holderType + ")) {\n"; // start if 2
            method += "        " + invokeSetter + ";\n";
            method += "    } else { \n"; // else if 2
            method += "        " + holderType + " holder = (" + holderType + ") value; \n";
            method += "        if (holder.hasValue()) {\n"; // start if 4
            method += "            value = holder.getValue();\n";
            method += "            " + invokeSetter + ";\n";
            method += "        }\n"; // end if 4
            method += "    }\n"; // end if 2
        }

        method += "    }\n"; // end of for
        method += "}"; // end of method

        cc.addMethod(CtMethod.make(method, cc));
    }
}
