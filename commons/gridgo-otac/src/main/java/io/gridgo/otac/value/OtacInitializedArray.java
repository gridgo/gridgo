package io.gridgo.otac.value;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.gridgo.otac.OtacType;
import io.gridgo.otac.exception.OtacException;
import lombok.Getter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacInitializedArray extends OtacValue {
    private OtacType type;

    @Singular
    private List<OtacValue> initValues;

    @Override
    public OtacType inferredType() {
        var type = this.type;
        if (type == null)
            for (var v : initValues) {
                if (type == null) {
                    type = v.inferredType();
                } else {
                    if (!v.inferredType().equals(type)) {
                        type = OtacType.OBJECT;
                        break;
                    }
                }
            }
        return type;
    }

    @Override
    public String toString() {
        if (initValues == null || initValues.isEmpty())
            throw new OtacException("array expected for size or values");

        var type = inferredType();

        var sb = new StringBuilder();
        sb.append("new ");
        sb.append(type.toString().trim());
        sb.append("[] ").append("{ ");
        sb.append(initValues.get(0));
        for (int i = 1; i < initValues.size(); i++)
            sb.append(", ").append(initValues.get(i).toString().trim());
        sb.append(" }");

        return sb.toString();
    }

    @Override
    public Set<Class<?>> requiredImports() {
        var imports = new HashSet<Class<?>>();
        if (type != null)
            imports.addAll(type.requiredImports());
        if (initValues != null)
            for (var p : initValues)
                imports.addAll(p.requiredImports());
        return imports;
    }
}
