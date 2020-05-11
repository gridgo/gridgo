package io.gridgo.pojo.builder.template;

import io.gridgo.otac.code.line.OtacLine;
import io.gridgo.otac.value.OtacValue;
import io.gridgo.pojo.translator.PojoUnboxer;
import io.gridgo.utils.StringUtils;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class WriteUnboxableField extends PojoSchemaTemplate<OtacLine> {

    private final @NonNull PojoUnboxer unboxer;

    private final @NonNull String outputVariableName;

    private final @NonNull String keyVariableName;

    private final @NonNull String unboxedVariableName;

    @Override
    public OtacLine apply() {

        var unboxerReturnType = unboxer.returnType();
        var typeName = unboxerReturnType.isArray() ? unboxerReturnType.getComponentType().getSimpleName()
                : unboxerReturnType.getSimpleName();
        var outputInvokedMethodName = "write" + StringUtils.upperCaseFirstLetter(typeName);
        if (unboxerReturnType.isArray())
            outputInvokedMethodName += "Array";

        return OtacLine.invokeMethod( //
                OtacValue.variable(outputVariableName), //
                outputInvokedMethodName, //
                OtacValue.variable(keyVariableName), //
                OtacValue.methodReturn( //
                        OtacValue.ofType(unboxer.declaringClass()), //
                        unboxer.methodName(), //
                        OtacValue.variable(unboxedVariableName)));
    }

}
