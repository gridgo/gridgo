package io.gridgo.otac;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class OtacParameter implements OtacRequireImports {

    @Getter
    @Singular("annotatedBy")
    private List<OtacAnnotation> annotations;

    @Getter
    @Builder.Default
    private boolean isFinal = false;

    @Getter
    private @NonNull String name;

    @Getter
    private @NonNull OtacType type;

    public static OtacParameter parameter(OtacType type, String name, OtacAnnotation... annotations) {
        return OtacParameter.builder() //
                .name(name) //
                .type(type) //
                .annotations(Arrays.asList(annotations)) //
                .build();
    }

    public static OtacParameter parameter(Class<?> type, String name, OtacAnnotation... annotations) {
        return parameter(OtacType.typeOf(type), name, annotations);
    }

    @Override
    public Set<Class<?>> requiredImports() {
        var imports = new HashSet<Class<?>>();
        imports.addAll(type.requiredImports());
        for (var a : getAnnotations())
            imports.addAll(a.requiredImports());
        return imports;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        for (var a : getAnnotations())
            sb.append(a.toString()).append(" ");
        sb.append(isFinal() ? "final " : "") //
                .append(type.toString()) //
                .append(getName());
        return sb.toString();
    }

}
