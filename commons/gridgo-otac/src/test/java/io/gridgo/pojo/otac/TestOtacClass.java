package io.gridgo.pojo.otac;

import static io.gridgo.otac.OtacAccessLevel.PRIVATE;
import static io.gridgo.otac.OtacAccessLevel.PUBLIC;
import static io.gridgo.otac.OtacAnnotation.annotation;
import static io.gridgo.otac.OtacGeneric.ANY;
import static io.gridgo.otac.OtacGeneric.genericDeclared;
import static io.gridgo.otac.OtacInheritOperator.EXTENDS;
import static io.gridgo.otac.OtacParameter.parameterOf;
import static io.gridgo.otac.OtacType.typeOf;
import static io.gridgo.otac.OtacValue.parameter;
import static io.gridgo.otac.code.OtacCodeLine.assignField;

import java.io.InputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.SimpleCompiler;
import org.junit.Test;

import io.gridgo.otac.OtacClass;
import io.gridgo.otac.OtacConstructor;
import io.gridgo.otac.OtacField;
import io.gridgo.otac.OtacGeneric;
import io.gridgo.otac.OtacType;
import io.gridgo.otac.OtacValue;
import io.gridgo.utils.annotations.ThreadSafe;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;

public class TestOtacClass {

    @SuppressWarnings("unchecked")
    @Test
    public void testSimpleClass() throws CompileException {

        var c = OtacClass.builder() //
                .accessLevel(PUBLIC) //
                .isAbstract(true) //
                .packageName("io.gridgo.pojo.otac.test") //
                .simpleClassName("TestOtacClassGenerate") //
                .annotatedBy(annotation(Builder.class)) //
                .generic(OtacGeneric.builder() //
                        .name("T") //
                        .operator(EXTENDS) //
                        .type(OtacType.builder() //
                                .type(Map.class) //
                                .genericType(ANY) //
                                .genericType(ANY) //
                                .build()) //
                        .build()) //
                .extendsFrom(typeOf(InputStream.class)) //
                .implement(typeOf(Serializable.class)) //
                .implement(OtacType.builder() //
                        .type(Set.class) //
                        .genericType(genericDeclared("T")) //
                        .build()) //
                .field(OtacField.builder() //
                        .accessLevel(PRIVATE) //
                        .name("stringField") //
                        .type(typeOf(String.class)) //
                        .annotatedBy(annotation(NonNull.class)) //
                        .generateGetter(true) //
                        .generateSetter(true) //
                        .build()) //
                .field(OtacField.builder() //
                        .accessLevel(PRIVATE) //
                        .name("stringArrField") //
                        .isFinal(true) //
                        .annotatedBy(annotation(Singular.class)) //
                        .type(typeOf(String[].class)) //
                        .generateGetter(true) //
                        .generateSetter(true) //
                        .build()) //
                .field(OtacField.builder() //
                        .accessLevel(PRIVATE) //
                        .name("booleanField") //
                        .type(typeOf(boolean.class)) //
                        .generateGetter(true) //
                        .generateSetter(true) //
                        .build()) //
                .field(OtacField.builder() //
                        .accessLevel(PRIVATE) //
                        .isFinal(true) //
                        .name("intField") //
                        .type(typeOf(int.class)) //
                        .generateGetter(true) //
                        .generateSetter(true) //
                        .build())
                .field(OtacField.builder() //
                        .accessLevel(PRIVATE) //
                        .name("intArrField") //
                        .type(typeOf(int[].class)) //
                        .generateGetter(true) //
                        .generateSetter(true) //
                        .build()) //
                .field(OtacField.builder() //
                        .accessLevel(PRIVATE) //
                        .name("innerSet") //
                        .type(OtacType.builder() //
                                .type(Set.class) //
                                .genericType(genericDeclared("T")) //
                                .build()) //
                        .initValue(OtacValue.newOf(//
                                OtacType.builder() //
                                        .type(HashSet.class) //
                                        .generic(true) //
                                        .build())) //
                        .generateGetter(true) //
                        .generateSetter(true) //
                        .build()) //
                .constructor(OtacConstructor.builder() //
                        .accessLevel(PUBLIC) //
                        .annotatedBy(annotation(ThreadSafe.class)) //
                        .parameter(parameterOf("intValue", typeOf(int.class))) //
                        .parameter(parameterOf("stringArr", typeOf(String[].class), NonNull.class))
                        .addLine(assignField("intField", parameter("intValue"))) //
                        .addLine(assignField("stringArrField", parameter("stringArr"))) //
                        .build()) //
                .build();

        String classContent = c.toString();
        try {
            new SimpleCompiler().cook(classContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(c.printWithLineNumber());
    }
}
