package io.gridgo.pojo.builder;

import static io.gridgo.utils.StringUtils.upperCaseFirstLetter;

import io.gridgo.otac.code.OtacCodeElement;
import io.gridgo.otac.code.block.OtacBlock;
import io.gridgo.otac.code.block.OtacElse;
import io.gridgo.otac.code.block.OtacIf;
import io.gridgo.otac.code.block.OtacTry;
import io.gridgo.otac.code.line.OtacLine;
import io.gridgo.otac.value.OtacValue;
import io.gridgo.pojo.output.PojoSchemaOutput;
import io.gridgo.pojo.reflect.type.PojoSimpleType;
import io.gridgo.pojo.translator.PojoUnboxer;
import io.gridgo.pojo.translator.PojoUnboxerRegistry;
import lombok.Builder;
import lombok.NonNull;

@Builder
public class SimpleTypeHandler {

    private final @NonNull String localValueName;
    private final @NonNull OtacValue outputKey;
    private final @NonNull PojoSimpleType fieldType;

    @Builder.Default
    private boolean isRoot = false;

    public OtacCodeElement genCode() {
        if (fieldType.isPrimitive() || fieldType.type() == String.class)
            return applyAsPrimitive();

        var rawType = fieldType.type();
        var unboxer = PojoUnboxerRegistry.getInstance().lookup(rawType);
        if (unboxer != null)
            return applyAsUnboxable(unboxer);

        return applyGenericPojo();
    }

    private OtacCodeElement applyAsPrimitive() {
        var typeName = fieldType.getSimpleName();
        var outputInvokedMethodName = "write" + upperCaseFirstLetter(typeName);

        var outputVariable = OtacValue.variable("output");
        return OtacLine.invokeMethod( //
                outputVariable, //
                outputInvokedMethodName, //
                outputKey, //
                OtacValue.variable(localValueName));
    }

    private OtacCodeElement applyAsUnboxable(PojoUnboxer unboxer) {
        var unboxerReturnType = unboxer.returnType();
        var typeName = unboxerReturnType.isArray() //
                ? unboxerReturnType.getComponentType().getSimpleName() //
                : unboxerReturnType.getSimpleName();

        var outputInvokedMethodName = "write" + upperCaseFirstLetter(typeName);

        return OtacBlock.builder() //
                .wrapped(false) //
                .addLine(OtacIf.builder() //
                        .condition(OtacLine.customLine(localValueName + " == null")) //
                        .addLine(OtacLine.invokeMethod(OtacValue.variable("output"), "writeNull", outputKey))
                        .orElse(OtacElse.builder() //
                                .addLine(OtacLine.invokeMethod( //
                                        OtacValue.variable("output"), //
                                        outputInvokedMethodName, //
                                        outputKey, //
                                        OtacValue.methodReturn( //
                                                OtacValue.ofType(unboxer.declaringClass()), //
                                                unboxer.methodName(), //
                                                OtacValue.variable(localValueName)))) //
                                .build())
                        .build()) //
                .build();

    }

    private OtacCodeElement applyGenericPojo() {
        var outputVariable = OtacValue.variable("output");
        var schemaFieldName = PojoConvension.genExtSchemaName(fieldType.type()); //

        var _output = OtacValue.variable(PojoConvension.SCHEMA_OUTPUT_VARNAME);
        var initSubOutput = isRoot //
                ? OtacLine.assignVariable( //
                        PojoConvension.SCHEMA_OUTPUT_VARNAME, //
                        OtacValue.methodReturn( //
                                outputVariable, //
                                "openSchema", //
                                outputKey)) //
                : OtacLine.declare(//
                        PojoSchemaOutput.class, //
                        PojoConvension.SCHEMA_OUTPUT_VARNAME, //
                        OtacValue.methodReturn( //
                                outputVariable, //
                                "openSchema", //
                                outputKey));
                        
        return OtacBlock.builder() //
                .wrapped(false) //
                .addLine(OtacIf.builder() // if the value is null, write null
                        .condition(OtacLine.customLine(localValueName + " == null")) //
                        .addLine(OtacLine.invokeMethod(outputVariable, "writeNull", outputKey))
                        .orElse(OtacElse.builder() // or else, try to open new schema output
                                .addLine(initSubOutput) //
                                .addLine(OtacTry.builder() // try
                                        .addLine( //
                                                OtacLine.invokeMethod( // invoke "serialize" on the opened schema output
                                                                       // above
                                                        OtacValue.field(schemaFieldName), //
                                                        "serialize", //
                                                        OtacValue.variable(localValueName), //
                                                        _output))
                                        .finallyDo( // finally
                                                OtacLine.invokeMethod( //
                                                        _output, //
                                                        "close")) //
                                        .build()) //
                                .build())
                        .build()) //
                .build();
    }
}
