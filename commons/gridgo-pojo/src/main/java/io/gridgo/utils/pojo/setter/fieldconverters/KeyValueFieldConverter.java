package io.gridgo.utils.pojo.setter.fieldconverters;

import java.util.HashMap;

import io.gridgo.utils.exception.UnsupportedTypeException;
import io.gridgo.utils.pojo.PojoMethodSignature;
import io.gridgo.utils.pojo.exception.PojoException;
import io.gridgo.utils.pojo.setter.data.GenericData;
import io.gridgo.utils.pojo.setter.data.KeyValueData;

public class KeyValueFieldConverter implements GenericDataConverter, FieldConverter<KeyValueData> {

    private static final KeyValueFieldConverter INSTANCE = new KeyValueFieldConverter();

    public static KeyValueFieldConverter getInstance() {
        return INSTANCE;
    }

    private KeyValueFieldConverter() {
        // Nothing to do
    }

    @Override
    public Object convert(KeyValueData data, PojoMethodSignature signature) {
        if (signature.isMapType())
            return keyValueToMap(data, signature);
        if (signature.isPojoType())
            return fromKeyValue(data, signature.getFieldType(), signature.getSetterProxy());
        throw new PojoException(
                "Cannot convert non key-value data to incompatible type: " + signature.getFieldType());
    }

    private Object keyValueToMap(KeyValueData data, PojoMethodSignature signature) {
        var map = new HashMap<String, Object>();
        for (var entry : data) {
            var value = toMapValue(entry.getValue(), signature);
            map.put(entry.getKey(), value);
        }
        return map;
    }

    private Object toMapValue(GenericData data, PojoMethodSignature signature) {
        var genericTypes = signature.getGenericTypes();
        var valueType = (genericTypes != null && genericTypes.length > 1) ? genericTypes[1] : Object.class;

        if (valueType == Object.class || data.isSequence() || data.isReference())
            return data.getInnerValue();

        if (data.isPrimitive())
            return data.asPrimitive().getDataAs(valueType);

        if (data.isKeyValue())
            return fromKeyValue(data.asKeyValue(), valueType, signature.getElementSetterProxy());

        throw new UnsupportedTypeException("Unknown entry value type: " + data.getClass());
    }
}
