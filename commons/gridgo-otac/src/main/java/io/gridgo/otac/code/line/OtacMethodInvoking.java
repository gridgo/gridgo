package io.gridgo.otac.code.line;

import java.util.Set;

import io.gridgo.otac.code.OtacInvokeMethod;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacMethodInvoking extends OtacLine {

    private @NonNull OtacInvokeMethod invokeMethod;

    @Override
    public Set<Class<?>> requiredImports() {
        return invokeMethod.requiredImports();
    }

    @Override
    public String toStringWithoutSemicolon() {
        return invokeMethod.toString();
    }
}
