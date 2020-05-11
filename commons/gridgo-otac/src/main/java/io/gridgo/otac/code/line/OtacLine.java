package io.gridgo.otac.code.line;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import io.gridgo.otac.OtacType;
import io.gridgo.otac.code.OtacCodeElement;
import io.gridgo.otac.code.OtacInvokeMethod;
import io.gridgo.otac.value.OtacValue;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class OtacLine extends OtacCodeElement {

    @Builder.Default
    private boolean addSemicolon = true;

    @Override
    public Set<Class<?>> requiredImports() {
        return Collections.emptySet();
    }

    public abstract String toStringWithoutSemicolon();

    public String toString() {
        return toStringWithoutSemicolon() + (addSemicolon ? ";" : "");
    }

    public static OtacLine RETURN = OtacLineCustom.of("return");

    public static OtacLine BREAK = OtacLineCustom.of("break");

    public static OtacLine CONTINUE = OtacLineCustom.of("continue");

    public static final OtacLine assignVariable(String variableName, OtacValue value) {
        return OtacAssignVariable.builder() //
                .name(variableName) //
                .value(value) //
                .build();
    }

    public static final OtacLine assignField(String variableName, OtacValue value) {
        return assignField(OtacValue.THIS, variableName, value);
    }

    public static final OtacLine assignField(OtacValue target, String variableName, OtacValue value) {
        return OtacAssignVariable.builder() //
                .target(target) //
                .name(variableName) //
                .value(value) //
                .build();
    }

    public static final OtacLine returnValue(OtacValue value) {
        return OtacReturnValue.builder().value(value).build();
    }

    public static final OtacLine declare(OtacType type, String variableName) {
        return OtacDeclareVariable.builder() //
                .name(variableName) //
                .type(type) //
                .build();
    }

    public static final OtacLine declare(OtacType type, String variableName, OtacValue initValue) {
        return OtacDeclareVariable.builder() //
                .name(variableName) //
                .type(type) //
                .initValue(initValue) //
                .build();
    }

    public static final OtacLine declare(Class<?> type, String variableName, Object initValue) {
        return declare(//
                OtacType.typeOf(type), //
                variableName, //
                initValue instanceof OtacValue //
                        ? (OtacValue) initValue //
                        : OtacValue.raw(initValue));
    }

    public static final OtacLine declare(String variableName, Object initValue) {
        return declare(OtacType.typeOf(initValue.getClass()), variableName, OtacValue.raw(initValue));
    }

    public static OtacLine invokeMethod(OtacValue target, String methodName, OtacValue... params) {
        return OtacMethodInvoking.builder() //
                .invokeMethod(OtacInvokeMethod.builder() //
                        .target(target) //
                        .methodName(methodName) //
                        .parameters(Arrays.asList(params)) //
                        .build()) //
                .build();
    }

    public static OtacLine invokeMethod(String methodName, OtacValue... params) {
        return invokeMethod(null, methodName, params);
    }

    public static OtacLine customLine(String content) {
        return OtacLineCustom.builder().content(content).build();
    }

}
