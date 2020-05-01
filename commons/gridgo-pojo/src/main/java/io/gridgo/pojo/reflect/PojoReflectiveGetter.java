package io.gridgo.pojo.reflect;

import io.gridgo.pojo.getter.PojoGetter;
import io.gridgo.pojo.support.PojoAccessorType;

public class PojoReflectiveGetter extends AbstractPojoReflectiveAccessor {

    private PojoGetter compiledCache = null;

    public PojoReflectiveGetter(String name, PojoReflectiveElement element) {
        super(PojoAccessorType.GET, name, element);
    }

    @Override
    public Class<?> fieldType() {
        var element = element();
        if (element.isField())
            return element.field().getType();
        return element.method().getReturnType();
    }

//    public OtacMethod nonCastedOtacMethod(OtacAccessLevel accessLevel, String methodName) {
//        var element = element();
//
//        var argName = "target";
//        var target = variable(argName);
//        var returnedValue = element.isField() //
//                ? field(target, element.field().getName()) //
//                : methodReturn(target, element.method().getName());
//
//        var returnType = element.isField() //
//                ? element.field().getType() //
//                : element.method().getReturnType();
//
//        return OtacMethod.builder() //
//                .name(methodName) //
//                .accessLevel(accessLevel) //
//                .returnType(typeOf(returnType)) //
//                .parameter(parameter(element.effectiveClass(), argName)) //
//                .addLine(returnValue(returnedValue)) //
//                .build();
//    }

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