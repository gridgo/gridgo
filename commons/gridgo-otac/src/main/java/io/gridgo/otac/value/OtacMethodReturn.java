package io.gridgo.otac.value;

import java.util.Set;

import io.gridgo.otac.code.OtacInvokeMethod;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacMethodReturn extends OtacValue {

    private @NonNull OtacInvokeMethod invokeMethod;

    @Override
    public Set<Class<?>> requiredImports() {
        return invokeMethod.requiredImports();
    }

    @Override
    public String toString() {
        return invokeMethod.toString();
    }
}
