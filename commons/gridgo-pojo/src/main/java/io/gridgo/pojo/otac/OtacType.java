package io.gridgo.pojo.otac;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacType extends OtacGenericDeclaration implements OtacRequireImports {

    public static final OtacType VOID = OtacType.builder().type(void.class).build();

    private @NonNull Class<?> type;

    public static OtacType of(Class<?> type) {
        return OtacType.builder().type(type).build();
    }

    public boolean isPrimitive() {
        return this.type.isPrimitive();
    }

    public boolean isArray() {
        return this.type.isArray();
    }

    @Override
    public Set<Class<?>> requiredImports() {
        var imports = new HashSet<Class<?>>();
        imports.addAll(super.requiredImports());
        if (type != null)
            imports.add(type);
        return imports;
    }

    @Override
    public String toString() {
        if (type == void.class || type == Void.TYPE)
            return "void"; // use for method only

        var sb = new StringBuilder();
        if (type.isPrimitive())
            sb.append(type.getName());
        else
            sb.append(type.getSimpleName());

        if (isGeneric()) {
            sb.append('<');
            var genericTypes = getGenericTypes();
            if (genericTypes != null && genericTypes.size() > 0) {
                sb.append(genericTypes.get(0).toString());
                for (int i = 1; i < genericTypes.size(); i++)
                    sb.append(", ").append(genericTypes.get(i).toString());
            }
            sb.append('>');
        }
        sb.append(" ");
        return sb.toString();
    }
}
