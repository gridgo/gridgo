package io.gridgo.otac;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.gridgo.otac.code.OtacCodeElement;
import io.gridgo.otac.utils.OtacUtils;
import lombok.Getter;
import lombok.Singular;
import lombok.experimental.Delegate;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class OtacConstructor extends OtacModifiers implements OtacRequireImports, OtacDeclaringClassAware {

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
        if (checkedExceptions != null)
            imports.addAll(checkedExceptions.requiredImports());
        if (parameters != null)
            for (var p : parameters)
                imports.addAll(p.requiredImports());
        return imports;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append(super.toString()).append(getDeclaringClass().getSimpleClassName()).append("(");

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
        if (body != null && !body.isEmpty()) {
            var tab = OtacUtils.tabs(1);
            for (var e : body)
                sb.append(tab).append(e.toString()).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
}
