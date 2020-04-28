package io.gridgo.pojo.setter;

import static io.gridgo.otac.OtacAccessLevel.PUBLIC;
import static io.gridgo.otac.OtacParameter.parameter;
import static io.gridgo.otac.OtacType.OBJECT;
import static io.gridgo.otac.OtacType.typeOf;
import static io.gridgo.otac.code.line.OtacLine.assignField;
import static io.gridgo.otac.code.line.OtacLine.invokeMethod;
import static io.gridgo.otac.value.OtacValue.castVariable;
import static io.gridgo.otac.value.OtacValue.variable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import io.gridgo.otac.OtacClass;
import io.gridgo.otac.OtacMethod;
import io.gridgo.otac.code.line.OtacLine;
import io.gridgo.pojo.support.MethodAccessorCompiler;

public abstract class AbstractSetterCompiler extends MethodAccessorCompiler implements SetterCompiler {

    protected OtacClass buildImplClass(Method method, Class<? extends PojoSetter> theInterface, String implMethodName) {
        var subfix = "_" + method.getName();

        var valueType = method.getParameterTypes()[0];
        var declaringClass = method.getDeclaringClass();
        var body = invokeMethod( //
                castVariable("target", declaringClass), //
                method.getName(), //
                theInterface == PojoSetter.class //
                        ? castVariable("value", valueType) //
                        : variable("value"));

        return build(theInterface, implMethodName, subfix, declaringClass, valueType, body);
    }

    protected OtacClass buildImplClass(Field field, Class<? extends PojoSetter> theInterface, String implMethodName) {
        var fieldName = field.getName();
        var subfix = "_setDirectField_" + fieldName;

        var valueType = field.getType();
        var declaringClass = field.getDeclaringClass();
        var body = assignField(castVariable("target", declaringClass), fieldName, variable("value"));

        return build(theInterface, implMethodName, subfix, declaringClass, valueType, body);
    }

    private OtacClass build(Class<? extends PojoSetter> theInterface, String implMethodName, String subfix,
            Class<?> declaringClass, Class<?> valueType, OtacLine body) {
        if (theInterface == PojoSetter.class)
            valueType = Object.class;

        var packageName = declaringClass.getPackageName();
        var simpleClassName = declaringClass.getSimpleName() + subfix;
        var classSrc = OtacClass.builder() //
                .accessLevel(PUBLIC) //
                .packageName(packageName) //
                .simpleClassName(simpleClassName) //
                .implement(typeOf(theInterface)) //
                .method(OtacMethod.builder() //
                        .accessLevel(PUBLIC) //
                        .name(implMethodName) //
                        .parameter(parameter(OBJECT, "target")) //
                        .parameter(parameter(valueType, "value")) //
                        .addLine(body) //
                        .build()) //
                .build();
        return classSrc;
    }
}
