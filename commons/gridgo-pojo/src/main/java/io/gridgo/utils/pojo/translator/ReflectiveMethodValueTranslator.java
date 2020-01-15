package io.gridgo.utils.pojo.translator;

import java.lang.reflect.Method;

import io.gridgo.utils.pojo.PojoMethodSignature;
import io.gridgo.utils.pojo.helper.BiFunctionAccessor;
import io.gridgo.utils.pojo.helper.MethodAccessors;

public class ReflectiveMethodValueTranslator implements ValueTranslator<Object, Object> {

    private final BiFunctionAccessor accessor;
    private final Class<?> acceptedType;

    ReflectiveMethodValueTranslator(Method method) {
        this.accessor = MethodAccessors.forStaticTwoParamsFunction(method);
        this.acceptedType = method.getParameterTypes()[0];
    }

    @Override
    public boolean translatable(Object obj) {
        return acceptedType.isInstance(obj);
    }

    @Override
    public Object translate(Object obj, PojoMethodSignature signature) {
        return this.accessor.apply(obj, signature);
    }
}
