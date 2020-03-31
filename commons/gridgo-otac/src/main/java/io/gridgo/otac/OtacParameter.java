package io.gridgo.otac;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @SuppressWarnings("unchecked")
    public static OtacParameter parameterOf(String name, OtacType type, Class<? extends Annotation>... annotations) {
        var otacAnnotations = Arrays.asList(annotations) //
                .stream() //
                .map(OtacAnnotation::annotation) //
                .collect(Collectors.toList());
        return OtacParameter.builder() //
                .name(name) //
                .type(type) //
                .annotations(otacAnnotations) //
                .build();
    }

    @Override
    public Set<Class<?>> requiredImports() {
        var imports = new HashSet<Class<?>>();
        imports.addAll(type.requiredImports());
        if (!getAnnotations().isEmpty())
            for (var a : getAnnotations())
                imports.addAll(a.requiredImports());
        return imports;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        if (!getAnnotations().isEmpty())
            for (var a : getAnnotations())
                sb.append(a.toString()).append(" ");
        sb.append(isFinal() ? "final " : "") //
                .append(type.toString()) //
                .append(getName());
        return sb.toString();
    }

}
