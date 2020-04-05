package io.gridgo.otac.code.line;

import io.gridgo.otac.OtacRequireImports;
import io.gridgo.otac.value.OtacValue;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Delegate;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacAssignVariable extends OtacLine {
    @Builder.Default
    private boolean isField = false;

    private @NonNull String name;

    @Delegate(types = OtacRequireImports.class)
    private @NonNull OtacValue value;

    @Override
    public String toStringWithoutSemicolon() {
        var sb = new StringBuilder();
        if (isField)
            sb.append("this.");
        sb.append(name).append(" = ").append(value);
        return sb.toString();
    }
}
