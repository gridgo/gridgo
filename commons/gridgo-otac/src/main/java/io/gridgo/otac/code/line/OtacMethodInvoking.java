package io.gridgo.otac.code.line;

import io.gridgo.otac.OtacRequireImports;
import io.gridgo.otac.code.OtacInvokeMethod;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Delegate;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacMethodInvoking extends OtacLine {

    @Delegate(types = OtacRequireImports.class)
    private @NonNull OtacInvokeMethod invokeMethod;

    @Override
    public String toStringWithoutSemicolon() {
        return invokeMethod.toString();
    }
}
