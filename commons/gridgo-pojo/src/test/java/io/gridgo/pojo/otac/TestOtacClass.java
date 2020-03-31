package io.gridgo.pojo.otac;

import static io.gridgo.pojo.otac.OtacAccessLevel.PRIVATE;
import static io.gridgo.pojo.otac.OtacGeneric.ANY;
import static io.gridgo.pojo.otac.OtacInheritOperator.EXTENDS;

import java.io.InputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.SimpleCompiler;
import org.junit.Test;

public class TestOtacClass {

    @Test
    public void testSimpleClass() throws CompileException {
        var c = OtacClass.builder() //
                .accessLevel(OtacAccessLevel.PUBLIC) //
                .isAbstract(true) //
                .packageName("io.gridgo.pojo.otac.test") //
                .simpleClassName("TestOtacClassGenerate") //
                .generic(OtacGeneric.builder() //
                        .name("T") //
                        .operator(EXTENDS) //
                        .type(OtacType.builder() //
                                .type(Map.class) //
                                .genericType(ANY) //
                                .genericType(ANY) //
                                .build()) //
                        .build()) //
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
                                .genericType(OtacGeneric.of("T")) //
                                .build()) //
                        .initValue(OtacValue.New.builder() //
                                .type(OtacType.builder() //
                                        .type(HashSet.class) //
                                        .generic(true) //
                                        .build())
                                .build()) //
                        .generateGetter(true) //
                        .generateSetter(true) //
                        .build()) //
                .build();

        String classContent = c.toString();
        System.out.println(classContent);
        new SimpleCompiler().cook(classContent);
    }
}
