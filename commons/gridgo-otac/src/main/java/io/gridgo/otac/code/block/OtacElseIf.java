package io.gridgo.otac.code.block;

import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class OtacElseIf extends OtacIf {

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append(" else ");
        sb.append(super.toString());
        return sb.toString();
    }
}
