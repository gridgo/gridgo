package io.gridgo.pojo.otac;

import static io.gridgo.otac.OtacAccessLevel.PRIVATE;
import static io.gridgo.otac.OtacGeneric.ANY;
import static io.gridgo.otac.OtacInheritOperator.EXTENDS;

import java.io.InputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.SimpleCompiler;
import org.junit.Test;

import io.gridgo.otac.OtacAccessLevel;
import io.gridgo.otac.OtacClass;
import io.gridgo.otac.OtacField;
import io.gridgo.otac.OtacGeneric;
import io.gridgo.otac.OtacType;
import io.gridgo.otac.OtacValue;

public class TestOtacClass {

    @Test
    public void testSimpleClass() throws CompileException {
        var classGenericT = OtacGeneric.builder() //
                .name("T") //
                .operator(EXTENDS) //
                .type(OtacType.builder() //
                        .type(Map.class) //
                        .genericType(ANY) //
                        .genericType(ANY) //
                        .build()) //
                .build();

        var c = OtacClass.builder() //
                .accessLevel(OtacAccessLevel.PUBLIC) //
                .isAbstract(true) //
                .packageName("io.gridgo.pojo.otac.test") //
                .simpleClassName("TestOtacClassGenerate") //
                .generic(classGenericT) //
                .extendsFrom(OtacType.of(InputStream.class)) //
                .implement(OtacType.of(Serializable.class)) //
                .implement(OtacType.builder() //
                        .type(Set.class) //
                        .genericType(OtacGeneric.of("T")) //
                        .build()) //
                .field(OtacField.builder() //
                        .accessLevel(PRIVATE) //
                        .name("stringField") //
                        .type(OtacType.of(String.class)) //
                        .generateGetter(true) //
                        .generateSetter(true) //
                        .build()) //
                .field(OtacField.builder() //
                        .accessLevel(PRIVATE) //
                        .name("stringArrField") //
                        .type(OtacType.of(String[].class)) //
                        .generateGetter(true) //
                        .generateSetter(true) //
                        .initValue(OtacValue.InitializedArray.builder() //
                                .initValue(OtacValue.raw("this is test text")) //
                                .initValue(OtacValue.raw("text2")) //
                                .build()) //
                        .build()) //
                .field(OtacField.builder() //
                        .accessLevel(PRIVATE) //
                        .name("booleanField") //
                        .type(OtacType.of(boolean.class)) //
                        .generateGetter(true) //
                        .generateSetter(true) //
                        .build()) //
                .field(OtacField.builder() //
                        .accessLevel(PRIVATE) //
                        .name("intField") //
                        .type(OtacType.of(int.class)) //
                        .generateGetter(true) //
                        .generateSetter(true) //
                        .initValue(OtacValue.Raw.of(111)) //
                        .build())
                .field(OtacField.builder() //
                        .accessLevel(PRIVATE) //
                        .name("intArrField") //
                        .type(OtacType.of(int[].class)) //
                        .generateGetter(true) //
                        .generateSetter(true) //
                        .build()) //
                .field(OtacField.builder() //
                        .accessLevel(PRIVATE) //
                        .name("innerSet") //
                        .isFinal(true) //
                        .type(OtacType.builder() //
                                .type(Set.class) //
                                .generic(true) //
                                .genericType(OtacGeneric.of(classGenericT.getName())) //
                                .build()) //
                        .initValue(OtacValue.newOf(//
                                OtacType.builder() //
                                        .type(HashSet.class) //
                                        .generic(true) //
                                        .build())) //
                        .generateGetter(true) //
                        .generateSetter(true) //
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
