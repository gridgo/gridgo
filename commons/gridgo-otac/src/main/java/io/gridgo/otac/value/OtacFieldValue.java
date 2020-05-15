package io.gridgo.otac.value;

import java.util.Set;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacFieldValue extends OtacVariableValue {

    @Builder.Default
    private OtacValue target = OtacValue.THIS;

    @Override
    public Set<Class<?>> requiredImports() {
        return target.requiredImports();
    }

    @Override
    public String toString() {
        return target.toString() + "." + getName();
    }
}
