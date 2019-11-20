package io.gridgo.utils.pojo.translator;

import java.lang.reflect.Method;

import lombok.NonNull;

@SuppressWarnings("rawtypes")
public class MethodValueTranslator implements ValueTranslator {

    private final FunctionAccessor accessor;
    private final Class<?> acceptedType;

    MethodValueTranslator(@NonNull Method method) {
        try {
            this.accessor = FunctionAccessorGenerator.generate(method);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.acceptedType = method.getParameterTypes()[0];
    }

    @Override
    public boolean canApply(Object obj) {
        return acceptedType.isInstance(obj);
    }

    @Override
    public Object translate(Object obj) {
        return this.accessor.apply(obj);
    }
}
