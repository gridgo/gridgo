package io.gridgo.otac.code.line;

import io.gridgo.otac.OtacRequireImports;
import io.gridgo.otac.value.OtacValue;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Delegate;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public final class OtacReturnValue extends OtacLine {

    @Delegate(types = OtacRequireImports.class)
    private @NonNull OtacValue value;

    @Override
    public String toStringWithoutSemicolon() {
        var sb = new StringBuilder();
        sb.append("return ").append(value.toString());
        return sb.toString();
    }
}
