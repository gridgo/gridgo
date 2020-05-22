package io.gridgo.utils.pojo.setter;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.janino.SimpleCompiler;

import io.gridgo.utils.PrimitiveUtils;
import io.gridgo.utils.pojo.AbstractProxyBuilder;
import io.gridgo.utils.pojo.MethodSignatureExtractor;
import io.gridgo.utils.pojo.PojoMethodSignature;
import io.gridgo.utils.pojo.exception.PojoProxyException;

class JaninoSetterProxyBuilder extends AbstractProxyBuilder implements PojoSetterProxyBuilder {

    private static final MethodSignatureExtractor EXTRACTOR = SetterMethodSignatureExtractor.getInstance();

    @Override
    public PojoSetterProxy buildSetterProxy(Class<?> target) {
        var classContent = new StringBuilder();

        var targetType = target.getSimpleName();
        var methodSignatures = EXTRACTOR.extractMethodSignatures(target);
        var allFields = buildAllFields(methodSignatures);

        classContent.append(buildGetFieldsMethod(allFields)).append("\n\n");
        classContent.append(buildSignaturesFieldAndMethod(methodSignatures)).append("\n\n");
        classContent.append(buildSignatureFields(methodSignatures)).append("\n\n");
        classContent.append(buildSetSignatureMethod(methodSignatures)).append("\n\n");
        classContent.append(buildApplyValueMethod(methodSignatures, targetType)).append("\n\n");
        classContent.append(buildWalkthroughAllMethod(methodSignatures, targetType)).append("\n\n");
        classContent.append(buildWalkthroughMethod(methodSignatures, targetType, allFields));

        var body = addTabToAllLine(1, classContent.toString());
        classContent = new StringBuilder();
        var packageName = target.getPackageName();
        var classSimpleName = "_" + target.getSimpleName() + "SetterProxy";
        classContent.append("package " + packageName + ";\n\n");

        doImport(classContent, PojoMethodSignature.class, PojoSetterProxy.class, PojoSetterConsumer.class, target,
                List.class, ArrayList.class);

        classContent.append("public final class " + classSimpleName + " implements "
                + PojoSetterProxy.class.getSimpleName() + " { \n");
        classContent.append(body);
        classContent.append("}");

        try {
            var compiler = new SimpleCompiler();
            compiler.cook(classContent.toString());
            var className = packageName + "." + classSimpleName;
            return makeProxy(methodSignatures, compiler.getClassLoader().loadClass(className));
        } catch (Exception e) {
            throw new PojoProxyException("Error while building setter proxy for class: " + target.getName(), e);
        }
    }

    private PojoSetterProxy makeProxy(List<PojoMethodSignature> methodSignatures, Class<?> resultClass)
            throws Exception {
        var result = (PojoSetterProxy) resultClass.getConstructor().newInstance();
        var signatureSetter = resultClass.getMethod("setMethodSignature", String.class, PojoMethodSignature.class);
        for (PojoMethodSignature methodSignature : methodSignatures)
            signatureSetter.invoke(result, methodSignature.getFieldName(), methodSignature);
        return result;
    }

    private String buildAllFields(List<PojoMethodSignature> methodSignatures) {
        var allFieldsBuilder = new StringBuilder();
        for (PojoMethodSignature methodSignature : methodSignatures) {
            if (allFieldsBuilder.length() > 0) {
                allFieldsBuilder.append(",");
            }
            allFieldsBuilder.append('"').append(methodSignature.getFieldName()).append('"');
        }
        var allFields = allFieldsBuilder.toString();
        return allFields;
    }

    private String buildApplyValueMethod(List<PojoMethodSignature> methodSignatures, String targetType) {
        String castedTarget = "(" + targetType + ") target";
        String method = "public void applyValue(Object target, String fieldName, Object value) { \n"; //
        method += "\t" + targetType + " castedTarget = " + castedTarget + ";\n";
        method += "\tswitch (fieldName) {\n"; // start switch
        for (PojoMethodSignature methodSignature : methodSignatures) {
            String fieldName = methodSignature.getFieldName();
            String invokeSetter = buildInvokeSetter(methodSignature);
            method += "\tcase \"" + fieldName + "\": " + invokeSetter + "; break; \n"; //
        }
        method += "\t}\n";// end switch
        method += "}";

        return method;
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
                    invokeSetter += "(value == null ? (" + wrapperTypeName + ") null : " + wrapperTypeName
                            + ".valueOf(((" + numberType + ") value)." + primitiveTypeName + "Value()))";
                }
            } else if (fieldType.isPrimitive()) {
                invokeSetter += "(((" + wrapperTypeName + ") value)." + fieldType.getName() + "Value())";
            } else {
                invokeSetter += "((" + wrapperTypeName + ") value)";
            }
        } else if (fieldType.isArray()) {
            String componentType = methodSignature.getComponentType().getName() + "[]";
            invokeSetter += "((" + componentType + ") value)";
        } else {
            invokeSetter += "((" + fieldType.getName() + ") value)";
        }
        return invokeSetter;
    }

    private String buildWalkthroughMethod(List<PojoMethodSignature> signatures, String targetType, String allFields) {

        var signatureFieldSubfix = "Signature";
        var castedTarget = "(" + targetType + ") target";

        String method = "public void walkThrough(Object target, PojoSetterConsumer consumer, String... fields) { \n"; //
        method += "    if (fields == null || fields.length == 0) {this.walkThroughAll(target, consumer); return;}\n";
        method += "    " + targetType + " castedTarget = " + castedTarget + ";\n";
        method += "    Object value = null;\n";
        method += "    for (int i=0; i < fields.length; i++) {\n"; // start for loop via fields
        method += "        String fieldName = fields[i];\n"; // create temp variable `fieldName`
        method += "        switch (fieldName) {\n";
        for (var signature : signatures) {
            var fieldName = signature.getFieldName();
            var invokeSetter = buildInvokeSetter(signature);
            var signatureFieldName = fieldName + signatureFieldSubfix;
            method += "        case \"" + fieldName + "\": \n"; // start case
            method += "            value = consumer.apply(this." + signatureFieldName + ");\n";
            method += "            if (value != null) {\n"; // start if 2
            method += "                " + invokeSetter + ";\n";
            method += "            }\n"; // end if 2
            method += "            break;\n"; // end case
        }
        method += "        }\n"; // end switch
        method += "    }"; // end of for
        method += "\n}"; // end of method

        return method;
    }

    private String buildWalkthroughAllMethod(List<PojoMethodSignature> methodSignatures, String targetType) {

        String signatureFieldSubfix = "Signature";
        String castedTarget = "(" + targetType + ") target";

        String method = "private void walkThroughAll(Object target, PojoSetterConsumer consumer) { \n"; //
        method += "    " + targetType + " castedTarget = " + castedTarget + ";\n";
        method += "    Object value = null;\n";

        for (PojoMethodSignature methodSignature : methodSignatures) {
            String fieldName = methodSignature.getFieldName();
            String invokeSetter = buildInvokeSetter(methodSignature);
            String signatureFieldName = fieldName + signatureFieldSubfix;
            method += "    value = consumer.apply(" + signatureFieldName + ");\n";
            method += "    if (value != null) {\n";
            method += "        " + invokeSetter + ";\n";
            method += "    }\n"; //
        }

        method += "}"; // end of method

        return method;
    }

}
