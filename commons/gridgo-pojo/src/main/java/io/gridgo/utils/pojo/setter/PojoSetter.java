package io.gridgo.utils.pojo.setter;

import java.lang.reflect.InvocationTargetException;

import io.gridgo.utils.pojo.PojoMethodSignature;
import io.gridgo.utils.pojo.exception.PojoProxyException;
import io.gridgo.utils.pojo.setter.data.GenericData;
import io.gridgo.utils.pojo.setter.data.KeyValueData;
import io.gridgo.utils.pojo.setter.fieldconverters.FieldConverter;
import io.gridgo.utils.pojo.setter.fieldconverters.GenericFieldConverter;
import lombok.NonNull;

public class PojoSetter {

    public static PojoSetter of(Object target, PojoSetterProxy proxy) {
        return new PojoSetter(target, proxy);
    }

    public static PojoSetter of(Object target) {
        return of(target, PojoSetterRegistry.DEFAULT.getSetterProxy(target.getClass()));
    }

    public static PojoSetter ofType(@NonNull Class<?> targetType, PojoSetterProxy proxy) {
        try {
            var target = targetType.getConstructor().newInstance();
            return of(target, proxy);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new PojoProxyException("Cannot create instance of class: " + targetType.getName(), e);
        }
    }

    public static PojoSetter ofType(Class<?> targetType) {
        try {
            var target = targetType.getConstructor().newInstance();
            return of(target, PojoSetterRegistry.DEFAULT.getSetterProxy(targetType));
        } catch (Exception e) {
            throw new PojoProxyException("Cannot create instance of class: " + targetType.getName(), e);
        }
    }

    /********************************************************
     ********************* END OF STATIC ********************
     ********************************************************/

    @NonNull
    private final Object data;

    @NonNull
    private final PojoSetterProxy proxy;

    @NonNull
    private KeyValueData source;

    private FieldConverter<GenericData> genericFieldConverter = GenericFieldConverter.getInstance();

    private PojoSetter(Object data, PojoSetterProxy proxy) {
        this.data = data;
        this.proxy = proxy;
    }

    public PojoSetter from(KeyValueData src) {
        this.source = src;
        return this;
    }

    public Object fill() {
        proxy.walkThrough(data, this::onField);
        return this.data;
    }

    private Object onField(PojoMethodSignature signature) {
        var value = getValue(signature);
        return genericFieldConverter.convert(value, signature);
    }

    private GenericData getValue(PojoMethodSignature signature) {
        var fieldName = signature.getFieldName();
        var transformedFieldName = signature.getTransformedFieldName();

        return transformedFieldName != null //
                ? source.getOrTake(transformedFieldName, () -> source.getOrDefault(fieldName, null)) //
                : source.getOrDefault(fieldName, null);
    }
}
