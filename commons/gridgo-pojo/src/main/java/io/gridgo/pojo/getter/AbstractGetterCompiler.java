package io.gridgo.pojo.getter;

import static io.gridgo.otac.OtacAccessLevel.PUBLIC;
import static io.gridgo.otac.OtacParameter.parameter;
import static io.gridgo.otac.OtacType.OBJECT;
import static io.gridgo.otac.OtacType.typeOf;
import static io.gridgo.otac.code.line.OtacLine.returnValue;
import static io.gridgo.otac.value.OtacValue.castVariable;
import static io.gridgo.otac.value.OtacValue.methodReturn;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import io.gridgo.otac.OtacClass;
import io.gridgo.otac.OtacMethod;
import io.gridgo.otac.code.line.OtacLine;
import io.gridgo.pojo.support.MethodAccessorCompiler;

public abstract class AbstractGetterCompiler extends MethodAccessorCompiler implements GetterCompiler {

    protected OtacClass buildImplClass(Method method, Class<? extends PojoGetter> theInterface, String implMethodName) {
        var returnType = method.getReturnType();
        var declaringClass = method.getDeclaringClass();
        var packageName = declaringClass.getPackageName();
        var classSrc = OtacClass.builder() //
                .accessLevel(PUBLIC) //
                .packageName(packageName) //
                .simpleClassName(declaringClass.getSimpleName() + "_" + method.getName()) //
                .implement(typeOf(theInterface)) //
                .method(OtacMethod.builder() //
                        .accessLevel(PUBLIC) //
                        .name(implMethodName) //
                        .returnType(typeOf(returnType)) //
                        .parameter(parameter(OBJECT, "target")) //
                        .addLine(returnValue(methodReturn(castVariable("target", declaringClass), method.getName()))) //
                        .build()) //
                .build();
        return classSrc;
    }

    protected OtacClass buildImplClass(Field field, Class<? extends PojoGetter> theInterface, String implMethodName) {
        var returnType = field.getType();
        var declaringClass = field.getDeclaringClass();
        var packageName = declaringClass.getPackageName();
        var classSrc = OtacClass.builder() //
                .accessLevel(PUBLIC) //
                .packageName(packageName) //
                .simpleClassName(declaringClass.getSimpleName() + "_getDirect_" + field.getName()) //
                .implement(typeOf(theInterface)) //
                .method(OtacMethod.builder() //
                        .accessLevel(PUBLIC) //
                        .name(implMethodName) //
                        .returnType(typeOf(returnType)) //
                        .parameter(parameter(declaringClass, "target")) //
                        .addLine(OtacLine
                                .customLine("return ((" + declaringClass.getName() + ") target)." + field.getName())) //
                        .build()) //
                .build();
        return classSrc;
    }

}
