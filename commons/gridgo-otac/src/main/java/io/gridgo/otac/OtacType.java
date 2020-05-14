package io.gridgo.otac;

import java.util.HashSet;
import java.util.Set;

import io.gridgo.otac.OtacType.OtacExplicitlyType.OtacExplicitlyTypeBuilder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class OtacType extends OtacGenericDeclaration implements OtacRequireImports {

    public static final OtacType VOID = OtacExplicitlyType.builder().type(void.class).build();

    public static final OtacType OBJECT = OtacExplicitlyType.builder().type(Object.class).build();

    public static final OtacType STRING = OtacExplicitlyType.builder().type(String.class).build();

    @Getter
    @SuperBuilder
    public static class OtacExplicitlyType extends OtacType {

        private @NonNull Class<?> type;

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof OtacExplicitlyType))
                return false;
            return this.type.equals(((OtacExplicitlyType) obj).type);
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

        @Override
        public String getSimpleName() {
            return type.getSimpleName();
        }
    }

    @Getter
    @SuperBuilder
    private static class OtacCustomType extends OtacType {
        private final @NonNull String typeName;

        @Override
        public String toString() {
            return typeName;
        }

        @Override
        public String getSimpleName() {
            return typeName;
        }
    }

    public static OtacType typeOf(Class<?> type) {
        return OtacExplicitlyType.builder().type(type).build();
    }

    public static OtacExplicitlyTypeBuilder<?, ?> explicitlyBuilder() {
        return OtacExplicitlyType.builder();
    }

    public static OtacType customType(String typeName) {
        return OtacCustomType.builder().typeName(typeName).build();
    }

    public abstract String getSimpleName();
}
