package io.gridgo.otac.value;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacVariableValue extends OtacValue {
    private @NonNull String name;

    @Override
    public String toString() {
        return getName();
    }
}
