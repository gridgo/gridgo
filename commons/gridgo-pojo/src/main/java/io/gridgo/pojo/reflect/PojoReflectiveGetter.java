package io.gridgo.pojo.reflect;

import static io.gridgo.otac.OtacParameter.parameter;
import static io.gridgo.otac.OtacType.typeOf;
import static io.gridgo.otac.code.line.OtacLine.returnValue;
import static io.gridgo.otac.value.OtacValue.field;
import static io.gridgo.otac.value.OtacValue.methodReturn;
import static io.gridgo.otac.value.OtacValue.variable;

import io.gridgo.otac.OtacAccessLevel;
import io.gridgo.otac.OtacMethod;
import io.gridgo.pojo.getter.PojoGetter;
import io.gridgo.pojo.support.PojoAccessorType;

public class PojoReflectiveGetter extends AbstractPojoReflectiveAccessor {

    private PojoGetter compiledCache = null;

    public PojoReflectiveGetter(String name, PojoReflectiveElement element) {
        super(PojoAccessorType.GET, name, element);
    }

    public OtacMethod nonCastedOtacMethod(OtacAccessLevel accessLevel, String methodName) {
        var element = element();

        var argName = "target";
        var target = variable(argName);
        var returnedValue = element.isField() //
                ? field(target, element.field().getName()) //
                : methodReturn(target, element.method().getName());

        var returnType = element.isField() //
                ? element.field().getType() //
                : element.method().getReturnType();

        return OtacMethod.builder() //
                .name(methodName) //
                .accessLevel(accessLevel) //
                .returnType(typeOf(returnType)) //
                .parameter(parameter(element.effectiveClass(), argName)) //
                .addLine(returnValue(returnedValue)) //
                .build();
    }

    public PojoGetter compile() {
        if (compiledCache == null) {
            synchronized (this) {
                if (compiledCache == null) {
                    var element = element();
                    compiledCache = element.isField() //
                            ? PojoGetter.compile(element.field()) //
                            : PojoGetter.compile(element.method());
                }
            }
        }
        return compiledCache;
    }
}