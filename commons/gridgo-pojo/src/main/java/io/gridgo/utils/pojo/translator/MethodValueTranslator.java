package io.gridgo.utils.pojo.translator;

import java.lang.reflect.Method;

import io.gridgo.utils.helper.BiFunctionAccessor;
import io.gridgo.utils.helper.MethodAccessors;
import io.gridgo.utils.pojo.PojoMethodSignature;
import lombok.NonNull;

public class MethodValueTranslator implements ValueTranslator<Object, Object> {

    private final BiFunctionAccessor accessor;
    private final Class<?> acceptedType;

    MethodValueTranslator(@NonNull Method method) {
        try {
            if (method.getParameterCount() == 2)
                if (method.getParameterTypes()[1] != PojoMethodSignature.class)
                    throw new IllegalArgumentException(
                            "Method to be used as value translator must accept 2 params, the second must be "
                                    + PojoMethodSignature.class.getName());

            this.accessor = MethodAccessors.forStaticTwoParamsFunction(method);
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
