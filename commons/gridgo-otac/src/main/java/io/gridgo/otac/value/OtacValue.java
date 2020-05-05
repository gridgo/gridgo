package io.gridgo.otac.value;

import static io.gridgo.otac.OtacType.typeOf;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.gridgo.otac.OtacRequireImports;
import io.gridgo.otac.OtacType;
import io.gridgo.otac.code.OtacInvokeMethod;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class OtacValue implements OtacRequireImports {

    public boolean needParenthesesOnInvoke() {
        return false;
    }

    public boolean isInvokable() {
        return true;
    }

    @Override
    public Set<Class<?>> requiredImports() {
        return Collections.emptySet();
    }

    public OtacType inferredType() {
        return OtacType.OBJECT;
    }

    @SuperBuilder
    private static class OtacValueNull extends OtacValue {

        @Override
        public boolean isInvokable() {
            return false;
        }

        @Override
        public String toString() {
            return "null";
        }
    }

    @Getter
    @SuperBuilder
    public static class OtacValueClass extends OtacValue {
        private final @NonNull Class<?> type;

        @Override
        public Set<Class<?>> requiredImports() {
            var imports = new HashSet<Class<?>>();
            imports.addAll(super.requiredImports());
            imports.add(type);
            return imports;
        }

        @Override
        public String toString() {
            return type.getSimpleName() + ".class";
        }
    }

    @Getter
    @SuperBuilder
    public static class OtacValueType extends OtacValue {
        private final @NonNull Class<?> type;

        @Override
        public Set<Class<?>> requiredImports() {
            var imports = new HashSet<Class<?>>();
            imports.addAll(super.requiredImports());
            imports.add(type);
            return imports;
        }

        @Override
        public String toString() {
            return type.getSimpleName();
        }
    }

    public static final OtacValue NULL = OtacValueNull.builder().build();
    public static final OtacValue THIS = OtacThisValue.DEFAULT;
    public static final OtacValue SUPER = OtacSuperValue.DEFAULT;

    public static OtacValue superOf(Class<?> type) {
        if (type == null)
            return SUPER;
        return OtacSuperValue.builder() //
                .explicitSuper(OtacType.typeOf(type)) //
                .build();
    }

    public static OtacValue thisOf(Class<?> type) {
        if (type == null)
            return THIS;
        return OtacThisValue.builder() //
                .explicitThis(OtacType.typeOf(type)) //
                .build();
    }

    public static OtacValue raw(Object value) {
        return OtacRawValue.of(value);
    }

    public static OtacValue newOf(Class<?> type) {
        return newOf(typeOf(type));
    }

    public static OtacValue newOf(OtacType type) {
        return OtacNewValue.of(type);
    }

    public static OtacValue field(String fieldName) {
        return OtacFieldValue.builder().name(fieldName).build();
    }

    public static OtacValue field(OtacValue target, String fieldName) {
        return OtacFieldValue.builder() //
                .target(target) //
                .name(fieldName) //
                .build();
    }

    public static OtacValue variable(String variableName) {
        return OtacVariableValue.builder().name(variableName).build();
    }

    public static OtacValue parameter(String parameterName) {
        return OtacVariableValue.builder().name(parameterName).build();
    }

    public static OtacValue newSizedArray(int size, Class<?> type) {
        return OtacSizedArray.builder() //
                .type(typeOf(type)) //
                .arraySize(size) //
                .build();
    }

    public static OtacValue newInitializedRawArray(Class<?> type, Object... values) {
        var builder = OtacInitializedArray.builder() //
                .type(typeOf(type));
        for (var v : values)
            builder.initValue(OtacValue.raw(v));
        return builder.build();
    }

    public static OtacValue newInitializedArray(Class<?> type, OtacValue... values) {
        var builder = OtacInitializedArray.builder() //
                .type(typeOf(type));
        for (var v : values)
            builder.initValue(v);
        return builder.build();
    }

    public static OtacValue castVariable(String variableName, Class<?> castTo) {
        return OtacCastedValue.builder() //
                .target(variable(variableName)) //
                .castTo(typeOf(castTo)) //
                .build();
    }

    public static OtacValue castVariable(String variableName, Class<?> castTo, boolean forceArray) {
        return OtacCastedValue.builder() //
                .target(variable(variableName)) //
                .castTo(typeOf(castTo)) //
                .forceArray(forceArray) //
                .build();
    }

    public static OtacValue methodReturn(OtacValue target, String methodName, OtacValue... params) {
        return OtacMethodReturn.builder() //
                .invokeMethod(OtacInvokeMethod.builder() //
                        .target(target) //
                        .methodName(methodName) //
                        .parameters(Arrays.asList(params)) //
                        .build()) //
                .build();
    }

    public static OtacValue methodReturn(String methodName, OtacValue... params) {
        return methodReturn(null, methodName, params);
    }

    public static OtacValue customValue(String content) {
        return OtacValueCustom.builder().content(content).build();
    }

    public static OtacValue ofClass(Class<?> type) {
        return OtacValueClass.builder().type(type).build();
    }

    public static OtacValue ofType(Class<?> type) {
        return OtacValueType.builder().type(type).build();
    }
}
