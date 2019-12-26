package io.gridgo.utils.pojo.translator;

import java.lang.reflect.Method;

import io.gridgo.utils.helper.BiFunctionAccessor;
import io.gridgo.utils.helper.MethodAccessors;
import io.gridgo.utils.pojo.PojoMethodSignature;

public class ReflectiveMethodValueTranslator implements ValueTranslator<Object, Object> {

    private final BiFunctionAccessor accessor;
    private final Class<?> acceptedType;

    ReflectiveMethodValueTranslator(Method method) {
        this.accessor = MethodAccessors.forStaticTwoParamsFunction(method);
        try {
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
