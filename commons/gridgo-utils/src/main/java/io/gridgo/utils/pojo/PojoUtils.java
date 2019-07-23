package io.gridgo.utils.pojo;

import java.util.Map.Entry;
import java.util.function.BiConsumer;

import io.gridgo.utils.pojo.getter.PojoGetter;
import io.gridgo.utils.pojo.getter.PojoGetterGenerator;
import io.gridgo.utils.pojo.getter.PojoGetterRegistry;
import io.gridgo.utils.pojo.getter.PojoGetterSignature;
import io.gridgo.utils.pojo.getter.PojoGetterSignatures;
import io.gridgo.utils.pojo.setter.PojoSetter;
import io.gridgo.utils.pojo.setter.PojoSetterGenerator;
import io.gridgo.utils.pojo.setter.PojoSetterRegistry;
import io.gridgo.utils.pojo.setter.PojoSetterSignature;
import io.gridgo.utils.pojo.setter.PojoSetterSignatures;

public class PojoUtils {

    private static final PojoGetterRegistry GETTER_REGISTRY = new PojoGetterRegistry(
            PojoGetterGenerator.newJavassist());

    private static final PojoSetterRegistry SETTER_REGISTRY = new PojoSetterRegistry(
            PojoSetterGenerator.newJavassist());

    public static final void walkThroughGetters(Class<?> targetType, BiConsumer<String, PojoGetter> getterConsumer) {
        PojoGetterSignatures getters = GETTER_REGISTRY.getGetterSignatures(targetType);
        for (Entry<String, PojoGetterSignature> entry : getters) {
            getterConsumer.accept(entry.getKey(), entry.getValue().getGetter());
        }
    }

    public static final void walkThroughValueFromGetters(Object target, BiConsumer<String, Object> valueConsumer) {
        walkThroughGetters(target.getClass(),
                (fieldName, getter) -> valueConsumer.accept(fieldName, getter.get(target)));
    }

    public static final Object getValue(Object target, String fieldName) {
        return GETTER_REGISTRY.getGetterMethodSignature(target.getClass(), fieldName).getGetter().get(target);
    }

    public static final void setValue(Object target, String fieldName, Object value) {
        SETTER_REGISTRY.getSetterMethodSignature(target.getClass(), fieldName).getSetter().set(target, value);
    }

    public static final void walkThroughSetters(Class<?> targetType, BiConsumer<String, PojoSetter> setterConsumer) {
        PojoSetterSignatures setters = SETTER_REGISTRY.getSetterSignatures(targetType);
        for (Entry<String, PojoSetterSignature> entry : setters) {
            setterConsumer.accept(entry.getKey(), entry.getValue().getSetter());
        }
    }

    public static final void walkThroughAndSet(Object target, ValueProvider valueProvider) {
        walkThroughSetters(target.getClass(), (fieldName, setter) -> setter.set(target, valueProvider.get(fieldName)));
    }
}
