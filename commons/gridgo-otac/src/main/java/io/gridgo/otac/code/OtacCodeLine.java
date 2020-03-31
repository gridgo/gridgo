package io.gridgo.otac.code;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.gridgo.otac.OtacRequireImports;
import io.gridgo.otac.OtacType;
import io.gridgo.otac.OtacValue;
import io.gridgo.otac.exception.OtacException;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Delegate;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class OtacCodeLine extends OtacCodeElement {

    public static final AssignVariable assignVariable(String variableName, OtacValue value) {
        return AssignVariable.builder() //
                .name(variableName) //
                .value(value) //
                .build();
    }

    public static final AssignVariable assignField(String variableName, OtacValue value) {
        return AssignVariable.builder() //
                .isField(true) //
                .name(variableName) //
                .value(value) //
                .build();
    }

    public static Return RETURN_VOID = Return.builder().build();

    public static final Return returnVoid() {
        return RETURN_VOID;
    }

    public static final ReturnValue returnValue(OtacValue value) {
        return ReturnValue.builder().value(value).build();
    }

    @Override
    public Set<Class<?>> requiredImports() {
        return Collections.emptySet();
    }

    @Getter
    @SuperBuilder
    private static class Return extends OtacCodeLine {

        @Override
        public Set<Class<?>> requiredImports() {
            return Collections.emptySet();
        }

        @Override
        public String toString() {
            return "return;";
        }
    }

    @Getter
    @SuperBuilder
    public static final class ReturnValue extends Return {

        @Delegate(types = OtacRequireImports.class)
        private @NonNull OtacValue value;

        @Override
        public String toString() {
            var sb = new StringBuilder();
            sb.append("return ").append(value.toString()).append(";");
            return sb.toString();
        }
    }

    @Getter
    @SuperBuilder
    public static class AssignVariable extends OtacCodeLine {
        @Builder.Default
        private boolean isField = false;

        private @NonNull String name;

        @Delegate(types = OtacRequireImports.class)
        private @NonNull OtacValue value;

        @Override
        public String toString() {
            var sb = new StringBuilder();
            if (isField)
                sb.append("this.");
            sb.append(name).append(" = ").append(value).append(";");
            return sb.toString();
        }
    }

    @Getter
    @SuperBuilder
    public static class DeclareVariable extends OtacCodeLine {

        @Builder.Default
        private boolean isFinal = false;

        private OtacType type;

        private @NonNull String name;

        private OtacValue initValue;

        @Override
        public Set<Class<?>> requiredImports() {
            var imports = new HashSet<Class<?>>();
            if (type != null)
                imports.addAll(type.requiredImports());
            if (initValue != null)
                imports.addAll(initValue.requiredImports());
            return imports;
        }

        @Override
        public String toString() {
            if (type == null && initValue == null)
                throw new OtacException("type and initValue cannot be null together");
            var sb = new StringBuilder();
            if (isFinal)
                sb.append("final ");
            sb.append(type == null ? "var" : type.getType().getSimpleName()).append(" ");
            sb.append(name);
            if (initValue != null)
                sb.append(" = ").append(initValue);
            sb.append(";");
            return sb.toString();
        }
    }
}
