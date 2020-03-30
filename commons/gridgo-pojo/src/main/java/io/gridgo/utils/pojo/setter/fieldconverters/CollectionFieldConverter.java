package io.gridgo.utils.pojo.setter.fieldconverters;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import static io.gridgo.utils.ArrayUtils.toArray;
import static io.gridgo.utils.ArrayUtils.toPrimitiveArray;

import io.gridgo.utils.exception.UnsupportedTypeException;
import io.gridgo.utils.pojo.PojoFieldSignature;
import io.gridgo.utils.pojo.setter.data.SequenceData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CollectionFieldConverter implements GenericDataConverter, FieldConverter<SequenceData> {

    private static final CollectionFieldConverter INSTANCE = new CollectionFieldConverter();

    public static CollectionFieldConverter getInstance() {
        return INSTANCE;
    }

    private CollectionFieldConverter() {
        // Nothing to do
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object convert(SequenceData array, PojoFieldSignature signature) {
        var coll = createCollection(signature);

        var genericTypes = signature.getGenericTypes();
        var resultElementType = (genericTypes == null || genericTypes.length == 0) //
                ? Object.class //
                : genericTypes[0];

        if (resultElementType == Object.class) {
            coll.addAll(array.toList());
        } else {
            convertForSpecificType(array, signature, coll, resultElementType);
        }

        return coll;
    }

    private Collection<Object> createCollection(PojoFieldSignature signature) {
        if (signature.isSetType())
            return new HashSet<>();
        return new LinkedList<>();
    }

    private void convertForSpecificType(SequenceData array, PojoFieldSignature signature, Collection<Object> coll,
            Class<?> resultElementType) {
        for (var element : array) {
            if (element == null || element.isNull()) {
                if (!signature.isSetType()) {
                    coll.add(null);
                } else {
                    log.warn("got null value for field {}, target is a set which doesn't allow null, ignored",
                            signature.getFieldName());
                }
            } else if (element.isKeyValue()) {
                coll.add(fromKeyValue(element.asKeyValue(), resultElementType, signature.getElementSetterProxy()));
            } else if (element.isSequence()) {
                coll.add(fromSequence(element.asSequence(), resultElementType));
            } else if (element.isPrimitive()) {
                coll.add(fromPrimitive(element.asPrimitive(), resultElementType));
            } else if (element.isReference()) {
                coll.add(fromReference(element.asReference()));
            } else {
                throw new UnsupportedTypeException("Unknown element type: " + element.getClass());
            }
        }
    }

    private Object fromSequence(SequenceData element, Class<?> resultElementType) {
        var list = element.toList();
        if (!resultElementType.isArray()) {
            return list;
        }
        var compType = resultElementType.getComponentType();
        if (compType.isPrimitive())
            return toPrimitiveArray(list, compType);
        return toArray(compType, list);
    }
}
