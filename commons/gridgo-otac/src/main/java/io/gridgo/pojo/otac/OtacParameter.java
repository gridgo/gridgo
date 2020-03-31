package io.gridgo.pojo.otac;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Delegate;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor(staticName = "of")
public class OtacParameter implements OtacRequireImports {

    private @NonNull String name;

    @Delegate(types = OtacRequireImports.class)
    private @NonNull OtacType type;

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append(super.toString()) //
                .append(type.toString()) //
                .append(getName());
        return sb.toString();
    }

}
