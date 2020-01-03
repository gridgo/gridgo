package io.gridgo.utils.pojo.setter.fieldconverters;

import static io.gridgo.utils.pojo.setter.ValueHolder.NO_VALUE;

import io.gridgo.utils.pojo.PojoMethodSignature;
import io.gridgo.utils.pojo.exception.PojoProxyException;
import io.gridgo.utils.pojo.setter.data.GenericData;
import io.gridgo.utils.pojo.setter.data.KeyValueData;
import io.gridgo.utils.pojo.setter.data.PrimitiveData;
import io.gridgo.utils.pojo.setter.data.SequenceData;

public class GenericFieldConverter implements GenericDataConverter, FieldConverter<GenericData> {

    private static final GenericFieldConverter INSTANCE = new GenericFieldConverter();

    private FieldConverter<PrimitiveData> primitiveFieldConverter = PrimitiveFieldConverter.getInstance();

    private FieldConverter<SequenceData> sequenceFieldConverter = SequenceFieldConverter.getInstance();

    private FieldConverter<KeyValueData> keyValueFieldConverter = KeyValueFieldConverter.getInstance();

    public static GenericFieldConverter getInstance() {
        return INSTANCE;
    }

    private GenericFieldConverter() {
        // Nothing to do
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object convert(GenericData value, PojoMethodSignature signature) {
        var valueTranslator = signature.getValueTranslator();
        if (valueTranslator != null && valueTranslator.translatable(value))
            return valueTranslator.translate(value, signature);

        if (value == null)
            return NO_VALUE;

        var fieldType = signature.getFieldType();
        if (value.isPrimitive() && value.asPrimitive().getData() == null) {
            return fromNullPrimitive(fieldType);
        }

        var fieldName = signature.getFieldName();
        if (signature.isExtPrimitive()) {
            return fromPrimitive(value, signature, fieldName);
        }

        if (signature.isSequenceType()) {
            return fromSequence(value, signature, fieldName);
        }

        if (signature.isMapOrPojoType()) {
            return fromKeyValue(value, signature, fieldName);
        }

        return NO_VALUE;
    }

    private Object fromNullPrimitive(Class<?> fieldType) {
        if (fieldType.isPrimitive())
            return NO_VALUE;
        return null;
    }

    private Object fromKeyValue(GenericData value, PojoMethodSignature signature, String fieldName) {
        if (value.isReference() && signature.isPojoType())
            return value.asReference().getReference();

        if (!value.isKeyValue())
            throw new PojoProxyException("Field '" + fieldName + "' expected key-value, got " + value.getClass());

        return keyValueFieldConverter.convert(value.asKeyValue(), signature);
    }

    private Object fromSequence(GenericData value, PojoMethodSignature signature, String fieldName) {
        if (!value.isSequence())
            throw new PojoProxyException("Field '" + fieldName + "' expected sequence, got " + value.getClass());

        return sequenceFieldConverter.convert(value.asSequence(), signature);
    }

    private Object fromPrimitive(GenericData value, PojoMethodSignature signature, String fieldName) {
        if (!value.isPrimitive())
            throw new PojoProxyException("Field '" + fieldName + "' expected value data, got " + value.getClass());

        return primitiveFieldConverter.convert(value.asPrimitive(), signature);
    }
}
