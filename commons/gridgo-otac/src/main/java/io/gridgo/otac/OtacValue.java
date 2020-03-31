package io.gridgo.otac;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.gridgo.otac.exception.OtacException;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.Delegate;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class OtacValue implements OtacRequireImports {

    @SuperBuilder
    private static class OtacValueNull extends OtacValue {
        @Getter
        private static final OtacValueNull instance = OtacValueNull.builder().build();
    }

    public static final OtacValue NULL = OtacValueNull.getInstance();

    @Override
    public Set<Class<?>> requiredImports() {
        return Collections.emptySet();
    }

    public OtacType inferredType() {
        return OtacType.OBJECT;
    }

    public static Raw newRaw(Object value) {
        return Raw.of(value);
    }

    public static New newSimple(Class<?> type) {
        return newSimple(OtacType.of(type));
    }

    public static New newSimple(OtacType type) {
        return New.of(type);
    }

    public static Field forField(String fieldName) {
        return Field.builder().name(fieldName).build();
    }

    public static ClosureVariable forVariable(String variableName) {
        return ClosureVariable.builder().name(variableName).build();
    }

    public static SizedArray newSizedArray(int size, Class<?> type) {
        return SizedArray.builder() //
                .type(OtacType.of(type)) //
                .arraySize(size) //
                .build();
    }

    public static InitializedArray newInitializedRawArray(Class<?> type, Object... values) {
        var builder = InitializedArray.builder() //
                .type(OtacType.of(type));
        for (var v : values)
            builder.initValue(OtacValue.newRaw(v));
        return builder.build();
    }

    public static InitializedArray newInitializedArray(Class<?> type, OtacValue... values) {
        var builder = InitializedArray.builder() //
                .type(OtacType.of(type));
        for (var v : values)
            builder.initValue(v);
        return builder.build();
    }

    @Getter
    @SuperBuilder
    public static class ClosureVariable extends OtacValue {
        private @NonNull String name;

        @Override
        public String toString() {
            return getName();
        }
    }

    @Getter
    @SuperBuilder
    public static class Field extends ClosureVariable {

        @Override
        public String toString() {
            return "this." + getName();
        }
    }

    @Getter
    @SuperBuilder
    public static class Raw extends OtacValue {
        private Object value;

        public static Raw of(Object value) {
            return Raw.builder().value(value).build();
        }

        @Override
        public OtacType inferredType() {
            if (value == null)
                return super.inferredType();
            return OtacType.of(value.getClass());
        }

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

        public static New of(OtacType type) {
            return New.builder().type(type).build();
        }

        @Override
        public OtacType inferredType() {
            return type;
        }

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
    public static class SizedArray extends OtacValue {

        @Delegate(types = OtacRequireImports.class)
        private @NonNull OtacType type;

        @Builder.Default
        private int arraySize = -1;

        @Override
        public OtacType inferredType() {
            return type;
        }

        @Override
        public String toString() {
            if (arraySize < 0)
                throw new OtacException("array expected for size or values");

            var sb = new StringBuilder();
            sb.append("new ");
            sb.append(getType().toString().trim());

            sb.append("[").append(arraySize).append("]");
            return sb.toString();
        }
    }

    @Getter
    @SuperBuilder
    public static class InitializedArray extends OtacValue {
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
}
