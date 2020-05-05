package io.gridgo.utils.pojo.getter;

import static io.gridgo.otac.OtacAccessLevel.PRIVATE;
import static io.gridgo.otac.OtacAccessLevel.PUBLIC;
import static io.gridgo.otac.OtacGeneric.generic;
import static io.gridgo.otac.OtacParameter.parameter;
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
import static io.gridgo.utils.pojo.AbstractProxy.SIGNATURE_FIELD_SUBFIX;

import java.util.List;

import org.codehaus.janino.SimpleCompiler;

import io.gridgo.otac.OtacClass;
import io.gridgo.otac.OtacConstructor;
import io.gridgo.otac.OtacMethod;
import io.gridgo.otac.OtacType;
import io.gridgo.otac.code.block.OtacCase;
import io.gridgo.otac.code.block.OtacFor;
import io.gridgo.otac.code.block.OtacIf;
import io.gridgo.otac.code.block.OtacSwitch;
import io.gridgo.otac.code.line.OtacLine;
import io.gridgo.utils.pojo.AbstractProxy;
import io.gridgo.utils.pojo.AbstractProxyBuilder;
import io.gridgo.utils.pojo.MethodSignatureExtractor;
import io.gridgo.utils.pojo.PojoMethodSignature;
import io.gridgo.utils.pojo.exception.PojoException;
import lombok.NonNull;

class JaninoGetterProxyBuilder extends AbstractProxyBuilder implements PojoGetterProxyBuilder {

    private static final MethodSignatureExtractor EXTRACTOR = GetterMethodSignatureExtractor.getInstance();

    @Override
    public PojoGetterProxy buildGetterProxy(@NonNull Class<?> target) {

        var signatures = EXTRACTOR.extractMethodSignatures(target);
        var packageName = target.getPackageName();
        var classSimpleName = "_" + target.getSimpleName() + "GetterProxy";

        var clazz = OtacClass.builder() //
                .accessLevel(PUBLIC) //
                .packageName(packageName) //
                .simpleClassName(classSimpleName) //
                .extendsFrom(typeOf(AbstractProxy.class)) //
                .implement(typeOf(PojoGetterProxy.class)) //
                .fields(buildSignatureFields(signatures)) //
                .constructor(OtacConstructor.builder() //
                        .accessLevel(PUBLIC) //
                        .parameter(parameter(OtacType.builder() //
                                .type(List.class) //
                                .genericType(generic(PojoMethodSignature.class)) //
                                .build(), "signatures")) //
                        .addLine(customLine("super(signatures)")) //
                        .build()) //
                .method(buildGetValueMethod(target, signatures)) //
                .method(buildWalkThroughAllMethod(target, signatures)) //
                .method(buildWalkThroughMethod(target, signatures)) //
                .build();

        try {
            var simpleCompiler = new SimpleCompiler();
            simpleCompiler.cook(clazz.toString());
            var cls = simpleCompiler.getClassLoader().loadClass(clazz.getName());
            return (PojoGetterProxy) cls.getConstructor(List.class).newInstance(signatures);
        } catch (Exception e) {
            throw new PojoException("error while building getter proxy for class: " + target.getName() + "\n"
                    + clazz.printWithLineNumber(), e);
        }
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
                .parameter(parameter(OBJECT, "target")) //
                .parameter(parameter(STRING, "fieldName")) //
                .addLine(declare(typeOf(type), "castedTarget", castVariable("target", type))) //
                .addLine(switchBuilder.build()) //
                .addLine(returnValue(NULL)) //
                .build();
    }

    private OtacMethod buildWalkThroughMethod(Class<?> type, List<PojoMethodSignature> methodSignatures) {

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
                .parameter(parameter(OBJECT, "target")) //
                .parameter(parameter(typeOf(PojoGetterConsumer.class), "consumer")) //
                .parameter(parameter(typeOf(String[].class), "fields")) //
                .addLine(OtacIf.builder() //
                        .condition(customValue("fields == null || fields.length == 0")) //
                        .addLine(invokeMethod("walkThroughAll", variable("target"), variable("consumer"))) //
                        .addLine(OtacLine.RETURN) //
                        .build())
                .addLine(declare(typeOf(type), "castedTarget", castVariable("target", type))) //
                .addLine(OtacFor.builder() //
                        .init(declare(int.class, "i", 0)) //
                        .condition(customLine("i < fields.length")) //
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
                .parameter(parameter(OBJECT, "target")) //
                .parameter(parameter(typeOf(PojoGetterConsumer.class), "consumer")) //
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
