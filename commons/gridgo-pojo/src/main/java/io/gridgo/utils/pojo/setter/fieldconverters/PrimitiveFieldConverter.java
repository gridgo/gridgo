package io.gridgo.utils.pojo.setter.fieldconverters;

import io.gridgo.utils.PrimitiveUtils;
import io.gridgo.utils.pojo.PojoMethodSignature;
import io.gridgo.utils.pojo.exception.PojoProxyException;
import io.gridgo.utils.pojo.setter.data.PrimitiveData;

public class PrimitiveFieldConverter implements GenericDataConverter, FieldConverter<PrimitiveData> {

    private static final PrimitiveFieldConverter INSTANCE = new PrimitiveFieldConverter();

    public static PrimitiveFieldConverter getInstance() {
        return INSTANCE;
    }

    private PrimitiveFieldConverter() {
        // Nothing to do
    }

    @Override
    public Object convert(PrimitiveData data, PojoMethodSignature signature) {
        var value = data.getData();
        if (value == null)
            return null;

        if (signature.getFieldType().isAssignableFrom(value.getClass()))
            return value;

        var fieldType = signature.getFieldType();
        try {
            return PrimitiveUtils.getValueFrom(fieldType, value);
        } catch (Exception e) {
            var fieldName = signature.getFieldName();
            throw new PojoProxyException(
                    "Invalid value for field '" + fieldName + "', expected type: " + fieldType + ", got: " + value,
                    e);
        }
    }
}
