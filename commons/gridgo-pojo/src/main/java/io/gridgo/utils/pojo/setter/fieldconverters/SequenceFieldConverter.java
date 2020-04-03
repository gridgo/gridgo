package io.gridgo.utils.pojo.setter.fieldconverters;

import io.gridgo.utils.pojo.PojoMethodSignature;
import io.gridgo.utils.pojo.exception.PojoException;
import io.gridgo.utils.pojo.setter.data.SequenceData;

public class SequenceFieldConverter implements GenericDataConverter, FieldConverter<SequenceData> {

    private static final SequenceFieldConverter INSTANCE = new SequenceFieldConverter();

    private FieldConverter<SequenceData> arrayFieldConverter = ArrayFieldConverter.getInstance();

    private FieldConverter<SequenceData> collectionFieldConverter = CollectionFieldConverter.getInstance();

    public static SequenceFieldConverter getInstance() {
        return INSTANCE;
    }

    private SequenceFieldConverter() {
        // Nothing to do
    }

    @Override
    public Object convert(SequenceData data, PojoMethodSignature signature) {
        if (signature.isCollectionType()) {
            return collectionFieldConverter.convert(data, signature);
        }
        if (signature.isArrayType()) {
            return arrayFieldConverter.convert(data, signature);
        }
        throw new PojoException("Cannot convert sequence data to incompatible type: " + signature.getFieldType());
    }
}
