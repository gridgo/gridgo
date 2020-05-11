package io.gridgo.pojo.builder.template;

import static io.gridgo.pojo.builder.PojoSchemaConvension.SEQUENCE_OUTPUT_VARNAME;

import java.util.List;

import io.gridgo.otac.code.OtacCodeElement;
import io.gridgo.otac.code.block.OtacElse;
import io.gridgo.otac.code.block.OtacTry;
import io.gridgo.otac.code.line.OtacLine;
import io.gridgo.otac.value.OtacValue;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class WriteNullOrOpenSequence extends WriteNullOrElse {

    private final @NonNull Class<?> elementType;

    @Singular("addLine")
    private List<OtacCodeElement> lines;

    @Override
    protected OtacElse elseBlock() {
//        var forLoop = OtacForeach.builder() //
//                .variableName("entry") //
//                .type(OtacType.typeOf(elementType)) //
//                .sequence(OtacValue.variable(getFieldName())) //
//                .addLine(WriteNullOrElse.);
//        getLines().forEach(forLoop::addLine);

        return OtacElse.builder() //
                .addLine(OtacLine.assignVariable(SEQUENCE_OUTPUT_VARNAME, //
                        OtacValue.methodReturn( //
                                OtacValue.variable(getOutput()), //
                                "openSequence", //
                                OtacValue.field(getOutputKey())))) //
                .addLine(OtacTry.builder() //
                        .addLine(OtacLine.customLine("int i = 0")) //
//                        .addLine(forLoop //
//                                .addLine(OtacLine.customLine("i++")) //
//                                .build()) //
                        .finallyDo(OtacLine.invokeMethod(OtacValue.variable(SEQUENCE_OUTPUT_VARNAME), "close")) //
                        .build()) //
                .build();
    }

}
