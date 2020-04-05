package io.gridgo.otac.code;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.gridgo.otac.value.OtacValue;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacInvokeMethod extends OtacCodeElement {

    private OtacValue target;

    private @NonNull String methodName;

    @Singular
    private List<OtacValue> parameters;

    @Override
    public Set<Class<?>> requiredImports() {
        var imports = new HashSet<Class<?>>();
        if (target != null)
            imports.addAll(target.requiredImports());
        if (!parameters.isEmpty())
            for (var p : parameters)
                imports.addAll(p.requiredImports());
        return imports;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        if (target != null) {
            if (!target.isInvokable())
                throw new IllegalArgumentException("Target " + target + " cannot be invoked");

            if (target.needParenthesesOnInvoke())
                sb.append("(");
            sb.append(target);
            if (target.needParenthesesOnInvoke())
                sb.append(")");
            sb.append(".");
        }
        sb.append(methodName).append("(");
        if (parameters != null && !parameters.isEmpty()) {
            sb.append(parameters.get(0));
            for (int i = 1; i < parameters.size(); i++)
                sb.append(", ").append(parameters.get(i));
        }
        sb.append(")");
        return sb.toString();
    }
}
