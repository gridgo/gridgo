package io.gridgo.utils.pojo.setter;

import static io.gridgo.otac.OtacAccessLevel.PRIVATE;
import static io.gridgo.otac.OtacAccessLevel.PUBLIC;
import static io.gridgo.otac.OtacGeneric.generic;
import static io.gridgo.otac.OtacParameter.parameter;
import static io.gridgo.otac.OtacType.OBJECT;
import static io.gridgo.otac.OtacType.typeOf;
import static io.gridgo.otac.code.line.OtacLine.BREAK;
import static io.gridgo.otac.code.line.OtacLine.RETURN;
import static io.gridgo.otac.code.line.OtacLine.assignVariable;
import static io.gridgo.otac.code.line.OtacLine.customLine;
import static io.gridgo.otac.code.line.OtacLine.declare;
import static io.gridgo.otac.code.line.OtacLine.invokeMethod;
import static io.gridgo.otac.value.OtacValue.NULL;
import static io.gridgo.otac.value.OtacValue.castVariable;
import static io.gridgo.otac.value.OtacValue.field;
import static io.gridgo.otac.value.OtacValue.methodReturn;
import static io.gridgo.otac.value.OtacValue.variable;
import static io.gridgo.utils.pojo.AbstractProxy.SIGNATURE_FIELD_SUBFIX;

import java.util.List;

import org.codehaus.janino.SimpleCompiler;

import io.gridgo.otac.OtacClass;
import io.gridgo.otac.OtacConstructor;
import io.gridgo.otac.OtacMethod;
import io.gridgo.otac.OtacType;
import io.gridgo.otac.code.block.OtacCase;
import io.gridgo.otac.code.block.OtacForeach;
import io.gridgo.otac.code.block.OtacIf;
import io.gridgo.otac.code.block.OtacSwitch;
import io.gridgo.otac.code.line.OtacLine;
import io.gridgo.otac.value.OtacValue;
import io.gridgo.utils.PrimitiveUtils;
import io.gridgo.utils.pojo.AbstractProxy;
import io.gridgo.utils.pojo.AbstractProxyBuilder;
import io.gridgo.utils.pojo.MethodSignatureExtractor;
import io.gridgo.utils.pojo.PojoMethodSignature;
import io.gridgo.utils.pojo.exception.PojoException;

class JaninoSetterProxyBuilder extends AbstractProxyBuilder implements PojoSetterProxyBuilder {

    private static final MethodSignatureExtractor EXTRACTOR = SetterMethodSignatureExtractor.getInstance();

    @Override
    public PojoSetterProxy buildSetterProxy(Class<?> target) {

        var signatures = EXTRACTOR.extractMethodSignatures(target);
        var packageName = target.getPackageName();
        var classSimpleName = "_" + target.getSimpleName() + "SetterProxy";

        var clazz = OtacClass.builder() //
                .accessLevel(PUBLIC) //
                .packageName(packageName) //
                .simpleClassName(classSimpleName) //
                .extendsFrom(typeOf(AbstractProxy.class)) //
                .implement(typeOf(PojoSetterProxy.class)) //
                .fields(buildSignatureFields(signatures)) //
                .constructor(OtacConstructor.builder() //
                        .accessLevel(PUBLIC) //
                        .parameter(parameter(OtacType.explicitlyBuilder() //
                                .type(List.class) //
                                .genericType(generic(PojoMethodSignature.class)) //
                                .build(), "signatures")) //
                        .addLine(customLine("super(signatures)")) //
                        .build()) //
                .method(buildApplyValueMethod(target, signatures)) //
                .method(buildWalkThroughAllMethod(target, signatures)) //
                .method(buildWalkThroughMethod(target, signatures)) //
                .build();

        try {
            var compiler = new SimpleCompiler();
            compiler.cook(clazz.toString());
            var cls = compiler.getClassLoader().loadClass(clazz.getName());
            return (PojoSetterProxy) cls.getConstructor(List.class).newInstance(signatures);
        } catch (Exception e) {
            throw new PojoException("Error while building setter proxy for class: " + target.getName() + ", code: \n"
                    + clazz.printWithLineNumber(), e);
        }
    }

    private OtacMethod buildApplyValueMethod(Class<?> type, List<PojoMethodSignature> methodSignatures) {
        var switchBuilder = OtacSwitch.builder().key(variable("fieldName"));
        for (var sig : methodSignatures) {
            var fieldName = sig.getFieldName();
            switchBuilder.addCase(OtacCase.builder() //
                    .value(OtacValue.raw(fieldName)) //
                    .curlyBracketsWrapped(false) //
                    .addLine(buildInvokeSetter(sig)) //
                    .addLine(BREAK) //
                    .build());
        }

        return OtacMethod.builder() //
                .accessLevel(PUBLIC) //
                .name("applyValue") //
                .parameter(parameter(OBJECT, "target")) //
                .parameter(parameter(String.class, "fieldName")) //
                .parameter(parameter(OBJECT, "value")) //
                .addLine(declare(type, "castedTarget", castVariable("target", type))) //
                .addLine(switchBuilder.build()) //
                .build();
    }

    private OtacLine buildInvokeSetter(PojoMethodSignature methodSignature) {
        var fieldType = methodSignature.getFieldType();
        OtacValue param;
        if (PrimitiveUtils.isNumberClass(fieldType)) {
            if (methodSignature.isPrimitiveType())
                param = methodReturn(castVariable("value", Number.class), fieldType.getTypeName() + "Value");
            else if (methodSignature.isWrapperType())
                param = methodReturn(castVariable("value", Number.class),
                        methodSignature.getPrimitiveTypeFromWrapperType().getTypeName() + "Value");
            else {
                // big decimal/big integer
                param = castVariable("value", fieldType);
            }
        } else if (methodSignature.isPrimitiveOrWrapperType()) {
            param = castVariable("value", methodSignature.getWrapperType());
            if (fieldType.isPrimitive())
                param = methodReturn(param, fieldType.getName() + "Value");
        } else if (fieldType.isArray()) {
            var componentType = methodSignature.getComponentType();
            param = castVariable("value", componentType, true);
        } else {
            param = castVariable("value", fieldType);
        }

        return invokeMethod(variable("castedTarget"), methodSignature.getMethodName(), param);
    }

    private OtacMethod buildWalkThroughMethod(Class<?> type, List<PojoMethodSignature> signatures) {
        var switchBuilder = OtacSwitch.builder().key(variable("fieldName"));
        for (var sig : signatures) {
            var fieldName = sig.getFieldName();
            var signatureFieldName = fieldName + SIGNATURE_FIELD_SUBFIX;
            switchBuilder//
                    .addCase(OtacCase.builder() //
                            .value(OtacValue.raw(fieldName)) //
                            .curlyBracketsWrapped(false) //
                            .addLine(assignVariable( //
                                    "value", //
                                    methodReturn( //
                                            variable("consumer"), //
                                            "apply", //
                                            field(signatureFieldName)))) //
                            .addLine(OtacIf.builder() //
                                    .condition(customLine("value != null")) //
                                    .addLine(buildInvokeSetter(sig)) //
                                    .build()) //
                            .addLine(BREAK) //
                            .build());
        }

        return OtacMethod.builder() //
                .accessLevel(PUBLIC) //
                .name("walkThrough") //
                .parameter(parameter(OBJECT, "target")) //
                .parameter(parameter(PojoSetterConsumer.class, "consumer")) //
                .parameter(parameter(String[].class, "fields")) //
                .addLine(OtacIf.builder() //
                        .condition(customLine("fields == null || fields.length == 0")) //
                        .addLine(invokeMethod("walkThroughAll", variable("target"), variable("consumer"))) //
                        .addLine(RETURN) //
                        .build()) //
                .addLine(declare(typeOf(type), "castedTarget", castVariable("target", type)))
                .addLine(declare(OBJECT, "value", NULL)) //
                .addLine(OtacForeach.builder() //
                        .type(typeOf(String.class))//
                        .variableName("fieldName") //
                        .sequence(variable("fields")) //
                        .addLine(switchBuilder.build()) //
                        .build()) //
                .build();
    }

    private OtacMethod buildWalkThroughAllMethod(Class<?> type, List<PojoMethodSignature> methodSignatures) {

        var methodBuilder = OtacMethod.builder() //
                .accessLevel(PRIVATE) //
                .name("walkThroughAll") //
                .parameter(parameter(OBJECT, "target")) //
                .parameter(parameter(PojoSetterConsumer.class, "consumer")) //
                .addLine(declare(type, "castedTarget", castVariable("target", type))) //
                .addLine(declare(OBJECT, "value", NULL));

        for (var sig : methodSignatures) {
            var fieldName = sig.getFieldName();
            var signatureFieldName = fieldName + SIGNATURE_FIELD_SUBFIX;

            methodBuilder //
                    .addLine(assignVariable( //
                            "value", //
                            methodReturn(//
                                    variable("consumer"), //
                                    "apply", //
                                    field(signatureFieldName)))) //
                    .addLine(OtacIf.builder() //
                            .condition(customLine("value != null")) //
                            .addLine(buildInvokeSetter(sig)) //
                            .build());

        }

        return methodBuilder.build();
    }

}
