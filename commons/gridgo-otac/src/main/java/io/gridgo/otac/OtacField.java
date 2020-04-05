package io.gridgo.otac;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.gridgo.otac.value.OtacValue;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.Delegate;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class OtacField extends OtacNamedElement implements OtacRequireImports, OtacDeclaringClassAware {

    @Getter
    @Singular("annotatedBy")
    private List<OtacAnnotation> annotations;

    @Delegate(types = OtacDeclaringClassAware.class)
    private final OtacDeclaringClassAware declaringClassHolder = OtacDeclaringClassAware.newInstance();

    @Getter
    @Builder.Default
    private boolean isVolatile = false;

    @Getter
    @Builder.Default
    private boolean isTransient = false;

    @Getter
    private @NonNull OtacType type;

    @Getter
    private OtacValue initValue;

    @Getter
    @Builder.Default
    private boolean generateSetter = false;

    @Getter
    @Builder.Default
    private boolean generateGetter = false;

    @Override
    public String toString() {
        var sb = new StringBuilder();
        if (!getAnnotations().isEmpty())
            for (var a : getAnnotations())
                sb.append(a.toString()).append("\n");
        sb.append(super.toString()) //
                .append(isTransient() ? "transient " : "") //
                .append(isVolatile() ? "volatile " : "") //
                .append(type.toString()) //
                .append(getName()) //
                .append(initValue != null ? (" = " + initValue.toString().trim()) : "") //
                .append(";");
        return sb.toString();
    }

    @Override
    public Set<Class<?>> requiredImports() {
        var imports = new HashSet<Class<?>>();
        imports.addAll(type.requiredImports());
        if (!getAnnotations().isEmpty())
            for (var a : getAnnotations())
                imports.addAll(a.requiredImports());
        if (initValue != null)
            imports.addAll(initValue.requiredImports());
        return imports;
    }
}
