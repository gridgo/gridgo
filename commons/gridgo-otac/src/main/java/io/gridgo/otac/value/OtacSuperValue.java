package io.gridgo.otac.value;

import java.util.Set;

import io.gridgo.otac.OtacType;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacSuperValue extends OtacValue {

    public static final OtacSuperValue DEFAULT = OtacSuperValue.builder().build();

    private OtacType explicitSuper;

    @Override
    public Set<Class<?>> requiredImports() {
        if (explicitSuper != null)
            return explicitSuper.requiredImports();
        return super.requiredImports();
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        if (explicitSuper != null)
            sb.append(explicitSuper).append(".");
        sb.append("super");
        return sb.toString();
    }
}
