package io.gridgo.utils.pojo.setter.fieldconverters;

import io.gridgo.utils.pojo.PojoFieldSignature;
import io.gridgo.utils.pojo.exception.PojoException;
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
    public Object convert(GenericData value, PojoFieldSignature signature) {
        var valueTranslator = signature.getValueTranslator();
        if (valueTranslator != null && valueTranslator.translatable(value))
            return valueTranslator.translate(value, signature);

        if (value == null)
            return null;

        if (value.isPrimitive() && value.asPrimitive().getData() == null) {
            return null;
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

        return null;
    }

    private Object fromKeyValue(GenericData value, PojoFieldSignature signature, String fieldName) {
        if (value.isReference() && signature.isPojoType())
            return value.asReference().getReference();

        if (!value.isKeyValue())
            throw new PojoException("Field '" + fieldName + "' expected key-value, got " + value.getClass());

        return keyValueFieldConverter.convert(value.asKeyValue(), signature);
    }

    private Object fromSequence(GenericData value, PojoFieldSignature signature, String fieldName) {
        if (!value.isSequence())
            throw new PojoException("Field '" + fieldName + "' expected sequence, got " + value.getClass());

        return sequenceFieldConverter.convert(value.asSequence(), signature);
    }

    private Object fromPrimitive(GenericData value, PojoFieldSignature signature, String fieldName) {
        if (!value.isPrimitive())
            throw new PojoException("Field '" + fieldName + "' expected value data, got " + value.getClass());

        return primitiveFieldConverter.convert(value.asPrimitive(), signature);
    }
}
