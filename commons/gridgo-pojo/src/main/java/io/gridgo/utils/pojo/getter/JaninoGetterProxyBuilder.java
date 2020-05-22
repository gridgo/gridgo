package io.gridgo.utils.pojo.getter;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.janino.SimpleCompiler;

import io.gridgo.utils.pojo.AbstractProxyBuilder;
import io.gridgo.utils.pojo.MethodSignatureExtractor;
import io.gridgo.utils.pojo.PojoMethodSignature;
import io.gridgo.utils.pojo.exception.PojoProxyException;
import lombok.NonNull;

class JaninoGetterProxyBuilder extends AbstractProxyBuilder implements PojoGetterProxyBuilder {

    private static final MethodSignatureExtractor EXTRACTOR = GetterMethodSignatureExtractor.getInstance();

    @Override
    public PojoGetterProxy buildGetterProxy(@NonNull Class<?> target) {

        var targetType = target.getSimpleName();
        var methodSignatures = EXTRACTOR.extractMethodSignatures(target);
        var allFields = createAllFields(methodSignatures);

        var packageName = target.getPackageName();
        var classSimpleName = "_" + target.getSimpleName() + "GetterProxy";
        var classContent = new StringBuilder();

        classContent.append(buildGetFieldsMethod(allFields)).append("\n\n");
        classContent.append(buildSignaturesFieldAndMethod(methodSignatures)).append("\n\n");
        classContent.append(buildSignatureFields(methodSignatures)).append("\n\n");
        classContent.append(buildSetSignatureMethod(methodSignatures)).append("\n\n");
        classContent.append(buildGetValueMethod(targetType, methodSignatures)).append("\n\n");
        classContent.append(buildWalkThroughAllMethod(targetType, methodSignatures)).append("\n\n");
        classContent.append(buildWalkThroughMethod(targetType, methodSignatures, allFields));

        var body = addTabToAllLine(1, classContent.toString());

        classContent = new StringBuilder();
        classContent.append("package " + packageName + ";\n\n");
        doImport(classContent, target, PojoGetterProxy.class, PojoMethodSignature.class, PojoGetterConsumer.class,
                List.class, ArrayList.class);
        classContent //
                .append("\n") //
                .append("public final class " + classSimpleName + " implements " + PojoGetterProxy.class.getSimpleName()
                        + " {\n");
        classContent.append(body);
        classContent.append("}"); // end class

        try {
            var simpleCompiler = new SimpleCompiler();
            simpleCompiler.cook(classContent.toString());
            var className = packageName + "." + classSimpleName;
            var cls = simpleCompiler.getClassLoader().loadClass(className);
            return makeProxy(methodSignatures, cls);
        } catch (Exception e) {
            throw new PojoProxyException("error while building getter proxy for class: " + target.getName(), e);
        }
    }

    private PojoGetterProxy makeProxy(List<PojoMethodSignature> methodSignatures, Class<?> resultClass)
            throws Exception {
        var result = (PojoGetterProxy) resultClass.getConstructor().newInstance();
        var signatureSetter = resultClass.getMethod("setMethodSignature", String.class, PojoMethodSignature.class);
        for (PojoMethodSignature methodSignature : methodSignatures)
            signatureSetter.invoke(result, methodSignature.getFieldName(), methodSignature);
        return result;
    }

    private String buildGetValueMethod(String typeName, List<PojoMethodSignature> methodSignatures) {
        String castedTarget = "(" + typeName + ") target";
        String method = "public Object getValue(Object target, String fieldName) { \n" //
                + "\t" + typeName + " castedTarget = " + castedTarget + ";\n"; //
        method += "\tswitch (fieldName) {\n";
        for (PojoMethodSignature methodSignature : methodSignatures) {
            String fieldName = methodSignature.getFieldName();
            String invoked = "castedTarget." + methodSignature.getMethodName() + "()";
            if (methodSignature.getFieldType().isPrimitive()) {
                invoked = methodSignature.getWrapperType().getName() + ".valueOf(" + invoked + ")";
            }
            method += "\tcase \"" + fieldName + "\":\n" //
                    + "\t\treturn " + invoked + "; \n"; //
        }
        method += "\t}\n"; // end switch
        method += "\treturn null;\n";
        method += "}";

        return method;
    }

    private String buildWalkThroughMethod(String typeName, List<PojoMethodSignature> methodSignatures,
            String allFields) {

        String signatureFieldSubfix = "Signature";

        String method = "public void walkThrough(Object target, PojoGetterConsumer consumer, String... fields) { \n";
        method += "    if (fields == null || fields.length == 0) { this.walkThroughAll(target, consumer); return; }\n";
        method += "    " + typeName + " castedTarget = (" + typeName + ") target;\n";
        method += "    for (int i=0; i<fields.length; i++) { \n";
        method += "        String field = fields[i];\n";
        method += "        switch (field) {\n";
        for (PojoMethodSignature methodSignature : methodSignatures) {
            String fieldName = methodSignature.getFieldName();
            String invokeGetter = "castedTarget." + methodSignature.getMethodName() + "()";
            if (methodSignature.getFieldType().isPrimitive()) {
                invokeGetter = methodSignature.getWrapperType().getName() + ".valueOf(" + invokeGetter + ")";
            }
            String signatureField = fieldName + signatureFieldSubfix;
            method += "        case \"" + fieldName + "\": \n";
            method += "            consumer.accept(this." + signatureField + ", " + invokeGetter + "); \n"; //
            method += "            break; \n";
        }
        method += "        }\n";// end switch
        method += "    }\n"; // end for
        method += "}";

        return method;
    }

    private String buildWalkThroughAllMethod(String typeName, List<PojoMethodSignature> methodSignatures) {

        String signatureFieldSubfix = "Signature";

        String method = "private void walkThroughAll(Object target, PojoGetterConsumer consumer) { \n";
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

        return method;
    }
}
