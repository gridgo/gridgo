package io.gridgo.otac.code.block;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class OtacElse extends OtacBlock {

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append(" else ");
        writeBodyTo(sb, 0, true);
        return sb.toString();
    }
}
