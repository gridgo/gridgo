package io.gridgo.otac.value;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacFieldValue extends OtacVariableValue {

    @Override
    public String toString() {
        return "this." + getName();
    }
}
