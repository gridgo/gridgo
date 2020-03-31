package io.gridgo.pojo.otac;

import static io.gridgo.pojo.otac.OtacUtils.tabs;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacMethod extends OtacNamedElement implements OtacRequireImports {

    @Singular
    private final List<OtacGeneric> generics;

    @Builder.Default
    private @NonNull OtacType returnType = OtacType.VOID;

    @Singular
    private final List<OtacParameter> parameters;

    private final OtacExceptionThrows checkedExceptions;

    @Builder.Default
    private String body = "";

    @Override
    public Set<Class<?>> requiredImports() {
        var imports = new HashSet<Class<?>>();
        imports.addAll(returnType.requiredImports());
        if (checkedExceptions != null)
            imports.addAll(checkedExceptions.requiredImports());
        if (generics != null)
            for (var g : generics)
                imports.addAll(g.requiredImports());
        if (parameters != null)
            for (var p : parameters)
                imports.addAll(p.requiredImports());
        return imports;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append(super.toString());
        if (generics != null && !generics.isEmpty()) {
            sb.append('<').append(generics.get(0).toString().trim());
            for (int i = 1; i < generics.size(); i++)
                sb.append(", ").append(generics.get(i).toString().trim());
            sb.append("> ");
        }

        sb.append(returnType.toString().trim()) //
                .append(" ") //
                .append(getName()) //
                .append('(');

        if (parameters != null && !parameters.isEmpty()) {
            var it = parameters.iterator();
            var entry = it.next();
            sb.append(entry.getType().toString().trim()).append(' ').append(entry.getName().trim());
            while (it.hasNext()) {
                entry = it.next();
                sb.append(", ") //
                        .append(entry.getType().toString().trim()) //
                        .append(' ') //
                        .append(entry.getName().trim());
            }
        }
        sb.append(") ");
        if (checkedExceptions != null)
            sb.append(checkedExceptions.toString());
        sb.append("{ \n");
        sb.append(tabs(1, body));
        sb.append("}");
        return sb.toString();
    }
}
