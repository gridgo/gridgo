package io.gridgo.otac.value;

import java.util.Set;

import io.gridgo.otac.OtacType;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacThisValue extends OtacValue {

    public static final OtacThisValue DEFAULT = OtacThisValue.builder().build();

    private OtacType explicitThis;

    @Override
    public Set<Class<?>> requiredImports() {
        if (explicitThis != null)
            return explicitThis.requiredImports();
        return super.requiredImports();
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        if (explicitThis != null)
            sb.append(explicitThis).append(".");
        sb.append("this");
        return sb.toString();
    }
}
