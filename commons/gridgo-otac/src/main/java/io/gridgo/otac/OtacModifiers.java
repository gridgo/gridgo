package io.gridgo.otac;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacModifiers extends OtacAccessControl {

    @Builder.Default
    private boolean isStatic = false;

    @Builder.Default
    private boolean isFinal = false;

    @Builder.Default
    private boolean isAbstract = false;

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append(super.toString()) //
                .append(isStatic() ? "static " : "") //
                .append(isFinal() ? "final " : "") //
                .append(isAbstract() ? "abstract " : "");
        return sb.toString();
    }
}
