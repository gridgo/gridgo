package io.gridgo.pojo.otac;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.gridgo.utils.pojo.exception.PojoException;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class OtacValue implements OtacRequireImports {

    @Override
    public Set<Class<?>> requiredImports() {
        return Collections.emptySet();
    }

    @Getter
    @SuperBuilder
    public static class Raw extends OtacValue {
        private Object value;

        @Override
        public String toString() {
            var sb = new StringBuilder();
            if (value == null) {
                sb.append("null");
            } else {
                if (value.getClass() == String.class)
                    sb.append('"');
                sb.append(value);
                if (value.getClass() == String.class)
                    sb.append('"');
            }
            return sb.toString();
        }
    }

    @Getter
    @SuperBuilder
    public static class New extends OtacValue {
        private @NonNull OtacType type;

        @Singular
        private List<OtacValue> parameters;

        @Override
        public String toString() {
            var sb = new StringBuilder();
            sb.append("new ");
            sb.append(type.toString().trim());
            sb.append("(");
            if (parameters != null && !parameters.isEmpty()) {
                sb.append(parameters.get(0));
                for (int i = 1; i < parameters.size(); i++)
                    sb.append(", ").append(parameters.get(i).toString().trim());
            }
            sb.append(")");
            return sb.toString();
        }

        @Override
        public Set<Class<?>> requiredImports() {
            var imports = new HashSet<Class<?>>();
            imports.addAll(type.requiredImports());
            if (parameters != null)
                for (var p : parameters)
                    imports.addAll(p.requiredImports());
            return imports;
        }
    }

    @Getter
    @SuperBuilder
    public static class NewArray extends OtacValue {
        private @NonNull OtacType type;

        @Builder.Default
        private int arraySize = -1;

        @Singular
        private List<OtacValue> initValues;

        @Override
        public String toString() {
            var sb = new StringBuilder();
            sb.append("new ");
            sb.append(getType().toString().trim());
            if (arraySize >= 0) {
                sb.append("[").append(arraySize).append("]");
            } else {
                sb.append("[] ").append("{ ");
                if (initValues != null && !initValues.isEmpty()) {
                    sb.append(initValues.get(0));
                    for (int i = 1; i < initValues.size(); i++)
                        sb.append(", ").append(initValues.get(i).toString().trim());
                } else {
                    throw new PojoException("array expected for size or values");
                }
                sb.append(" }");
            }
            return sb.toString();
        }

        @Override
        public Set<Class<?>> requiredImports() {
            var imports = new HashSet<Class<?>>();
            imports.addAll(type.requiredImports());
            if (initValues != null)
                for (var p : initValues)
                    imports.addAll(p.requiredImports());
            return imports;
        }
    }
}
