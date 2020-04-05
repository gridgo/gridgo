package io.gridgo.otac.value;

import io.gridgo.otac.OtacRequireImports;
import io.gridgo.otac.OtacType;
import io.gridgo.otac.exception.OtacException;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Delegate;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacSizedArray extends OtacValue {

    @Delegate(types = OtacRequireImports.class)
    private @NonNull OtacType type;

    @Builder.Default
    private int arraySize = -1;

    @Override
    public OtacType inferredType() {
        return type;
    }

    @Override
    public String toString() {
        if (arraySize < 0)
            throw new OtacException("array expected for size or values");

        var sb = new StringBuilder();
        sb.append("new ");
        sb.append(getType().toString().trim());

        sb.append("[").append(arraySize).append("]");
        return sb.toString();
    }
}
