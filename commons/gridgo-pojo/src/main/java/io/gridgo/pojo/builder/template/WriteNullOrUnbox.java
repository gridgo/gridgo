package io.gridgo.pojo.builder.template;

import io.gridgo.otac.code.block.OtacElse;
import io.gridgo.otac.code.line.OtacLine;
import io.gridgo.otac.value.OtacValue;
import io.gridgo.pojo.translator.PojoUnboxer;
import io.gridgo.utils.StringUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class WriteNullOrUnbox extends WriteNullOrElse {

    private final @NonNull PojoUnboxer unboxer;

    private final @NonNull String unboxedVarname;

    @Override
    protected OtacElse elseBlock() {
        var unboxerReturnType = unboxer.returnType();
        var typeName = unboxerReturnType.isArray() ? unboxerReturnType.getComponentType().getSimpleName()
                : unboxerReturnType.getSimpleName();
        var outputInvokedMethodName = "write" + StringUtils.upperCaseFirstLetter(typeName);
        if (unboxerReturnType.isArray())
            outputInvokedMethodName += "Array";

        var result = OtacElse.builder() //
                .addLine(OtacLine.invokeMethod( //
                        OtacValue.variable(getOutput()), //
                        outputInvokedMethodName, //
                        OtacValue.variable(getOutputKey()), //
                        OtacValue.methodReturn( //
                                OtacValue.ofType(unboxer.declaringClass()), //
                                unboxer.methodName(), //
                                OtacValue.variable(unboxedVarname)))) //
                .build();

        return result;
    }
}
