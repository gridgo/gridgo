package io.gridgo.utils.pojo.getter;

import static io.gridgo.otac.OtacAccessLevel.PRIVATE;
import static io.gridgo.otac.OtacAccessLevel.PUBLIC;
import static io.gridgo.otac.OtacParameter.parameterOf;
import static io.gridgo.otac.OtacType.OBJECT;
import static io.gridgo.otac.OtacType.STRING;
import static io.gridgo.otac.OtacType.typeOf;
import static io.gridgo.otac.code.line.OtacLine.customLine;
import static io.gridgo.otac.code.line.OtacLine.declare;
import static io.gridgo.otac.code.line.OtacLine.invokeMethod;
import static io.gridgo.otac.code.line.OtacLine.returnValue;
import static io.gridgo.otac.value.OtacValue.NULL;
import static io.gridgo.otac.value.OtacValue.castVariable;
import static io.gridgo.otac.value.OtacValue.customValue;
import static io.gridgo.otac.value.OtacValue.field;
import static io.gridgo.otac.value.OtacValue.methodReturn;
import static io.gridgo.otac.value.OtacValue.raw;
import static io.gridgo.otac.value.OtacValue.variable;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.janino.SimpleCompiler;

import io.gridgo.otac.OtacMethod;
import io.gridgo.otac.code.block.OtacCase;
import io.gridgo.otac.code.block.OtacFor;
import io.gridgo.otac.code.block.OtacIf;
import io.gridgo.otac.code.block.OtacSwitch;
import io.gridgo.otac.code.line.OtacLine;
import io.gridgo.utils.pojo.AbstractProxyBuilder;
import io.gridgo.utils.pojo.MethodSignatureExtractor;
import io.gridgo.utils.pojo.PojoMethodSignature;
import io.gridgo.utils.pojo.exception.PojoException;
import lombok.NonNull;

class JaninoGetterProxyBuilder extends AbstractProxyBuilder implements PojoGetterProxyBuilder {

    private static final MethodSignatureExtractor EXTRACTOR = GetterMethodSignatureExtractor.getInstance();
    private static final String SIGNATURE_FIELD_SUBFIX = "Signature";

    @Override
    public PojoGetterProxy buildGetterProxy(@NonNull Class<?> target) {

        var methodSignatures = EXTRACTOR.extractMethodSignatures(target);
        var allFields = createAllFields(methodSignatures);

        var packageName = target.getPackageName();
        var classSimpleName = "_" + target.getSimpleName() + "GetterProxy";
        var classContent = new StringBuilder();

        classContent.append(buildGetFieldsMethod(allFields)).append("\n\n");
        classContent.append(buildSignaturesFieldAndMethod(methodSignatures)).append("\n\n");
        classContent.append(buildSignatureFields(methodSignatures)).append("\n\n");
        classContent.append(buildSetSignatureMethod(methodSignatures)).append("\n\n");
        classContent.append(buildGetValueMethod(target, methodSignatures)).append("\n\n");
        classContent.append(buildWalkThroughAllMethod(target, methodSignatures)).append("\n\n");
        classContent.append(buildWalkThroughMethod(target, methodSignatures, allFields));

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
            throw new PojoException(
                    "error while building getter proxy for class: " + target.getName() + "\n" + classContent, e);
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

    private OtacMethod buildGetValueMethod(Class<?> type, List<PojoMethodSignature> methodSignatures) {
        var switchBuilder = OtacSwitch.builder().key(variable("fieldName"));
        for (var sig : methodSignatures) {
            switchBuilder.addCase(OtacCase.builder() //
                    .value(raw(sig.getFieldName())) //
                    .curlyBracketsWrapped(false) //
                    .addLine(returnValue(//
                            methodReturn( //
                                    variable("castedTarget"), //
                                    sig.getMethodName()))) //
                    .build());
        }

        return OtacMethod.builder() //
                .accessLevel(PUBLIC) //
                .name("getValue") //
                .returnType(OBJECT) //
                .parameter(parameterOf(OBJECT, "target")) //
                .parameter(parameterOf(STRING, "fieldName")) //
                .addLine(declare(typeOf(type), "castedTarget", castVariable("target", type))) //
                .addLine(switchBuilder.build()) //
                .addLine(returnValue(NULL)) //
                .build();
    }

    private OtacMethod buildWalkThroughMethod(Class<?> type, List<PojoMethodSignature> methodSignatures,
            String allFields) {

        var switchBuilder = OtacSwitch.builder().key(variable("field"));
        for (var methodSignature : methodSignatures) {
            String fieldName = methodSignature.getFieldName();
            String signatureField = fieldName + SIGNATURE_FIELD_SUBFIX;
            switchBuilder.addCase(OtacCase.builder() //
                    .value(raw(fieldName)) //
                    .curlyBracketsWrapped(false) //
                    .addLine(invokeMethod(variable("consumer"), "accept", field(signatureField),
                            methodReturn(variable("castedTarget"), methodSignature.getMethodName())))
                    .addLine(OtacLine.BREAK) //
                    .build());
        }

        return OtacMethod.builder() //
                .accessLevel(PUBLIC) //
                .name("walkThrough") //
                .parameter(parameterOf(OBJECT, "target")) //
                .parameter(parameterOf(typeOf(PojoGetterConsumer.class), "consumer")) //
                .parameter(parameterOf(typeOf(String[].class), "fields")) //
                .addLine(OtacIf.builder() //
                        .condition(customValue("fields == null || fields.length == 0")) //
                        .addLine(invokeMethod("walkThroughAll", variable("target"), variable("consumer"))) //
                        .addLine(OtacLine.RETURN) //
                        .build())
                .addLine(declare(typeOf(type), "castedTarget", castVariable("target", type))) //
                .addLine(OtacFor.builder() //
                        .init(declare(int.class, "i", 0)) //
                        .condition(customValue("i < fields.length")) //
                        .afterLoop(customLine("i++")) //
                        .addLine(declare(typeOf(String.class), "field", customValue("fields[i]"))) //
                        .addLine(switchBuilder.build()) //
                        .build())
                .build();
    }

    private OtacMethod buildWalkThroughAllMethod(Class<?> type, List<PojoMethodSignature> methodSignatures) {

        var builder = OtacMethod.builder() //
                .accessLevel(PRIVATE)//
                .name("walkThroughAll") //
                .parameter(parameterOf(OBJECT, "target")) //
                .parameter(parameterOf(typeOf(PojoGetterConsumer.class), "consumer")) //
                .addLine(declare(typeOf(type), "castedTarget", castVariable("target", type)));

        for (var sig : methodSignatures) {
            var fieldName = sig.getFieldName();
            var signatureField = fieldName + SIGNATURE_FIELD_SUBFIX;
            builder.addLine(//
                    invokeMethod( //
                            variable("consumer"), // target
                            "accept", // method name
                            field(signatureField), // param 1
                            methodReturn( // param 2
                                    variable("castedTarget"), //
                                    sig.getMethodName())));
        }

        return builder.build();
    }
}
