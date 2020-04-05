package io.gridgo.otac;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.gridgo.otac.code.OtacCodeElement;
import io.gridgo.otac.code.block.OtacBlock;
import lombok.Getter;
import lombok.Singular;
import lombok.experimental.Delegate;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class OtacConstructor extends OtacAccessControl implements OtacRequireImports, OtacDeclaringClassAware {

    @Getter
    @Singular("annotatedBy")
    private List<OtacAnnotation> annotations;

    @Delegate(types = OtacDeclaringClassAware.class)
    private final OtacDeclaringClassAware declaringClassHolder = OtacDeclaringClassAware.newInstance();

    @Getter
    @Singular
    private final List<OtacParameter> parameters;

    @Getter
    private final OtacExceptionThrows checkedExceptions;

    @Getter
    @Singular("addLine")
    private List<OtacCodeElement> body;

    @Override
    public Set<Class<?>> requiredImports() {
        var imports = new HashSet<Class<?>>();
        if (!getAnnotations().isEmpty())
            for (var a : getAnnotations())
                imports.addAll(a.requiredImports());
        if (checkedExceptions != null)
            imports.addAll(checkedExceptions.requiredImports());
        for (var p : parameters)
            imports.addAll(p.requiredImports());
        for (var e : body)
            imports.addAll(e.requiredImports());
        for (var a : annotations)
            imports.addAll(a.requiredImports());
        return imports;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        if (!getAnnotations().isEmpty())
            for (var a : getAnnotations())
                sb.append(a.toString()).append("\n");

        sb.append(super.toString()) //
                .append(getDeclaringClass().getSimpleClassName()) //
                .append("(");

        if (parameters != null && !parameters.isEmpty()) {
            sb.append(parameters.get(0).toString().trim());
            for (int i = 1; i < parameters.size(); i++) {
                sb.append(", ").append(parameters.get(i).toString().trim());
            }
        }

        sb.append(") ");
        if (checkedExceptions != null)
            sb.append(checkedExceptions.toString());
        if (body != null && !body.isEmpty())
            OtacBlock.of(body).writeBodyTo(sb, 0, true);
        else
            sb.append("{}");
        return sb.toString();
    }
}
