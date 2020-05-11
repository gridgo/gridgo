package io.gridgo.pojo.builder.template;

import io.gridgo.otac.code.block.OtacElse;
import io.gridgo.otac.code.block.OtacIf;
import io.gridgo.otac.code.line.OtacLine;
import io.gridgo.otac.value.OtacValue;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class WriteNullOrElse extends PojoSchemaTemplate<OtacIf> {

    private final @NonNull String output;

    private final @NonNull String ifIsNull;

    private final @NonNull String outputKey;

    @Override
    public OtacIf apply() {
        var result = OtacIf.builder() //
                .condition(OtacLine.customLine(getIfIsNull() + " == null")) //
                .addLine(OtacLine.invokeMethod( //
                        OtacValue.variable(getOutput()), //
                        "writeNull", //
                        OtacValue.variable(getOutputKey()))) //
                .orElse(elseBlock()) //
                .build();

        return result;
    }

    protected abstract OtacElse elseBlock();
}
