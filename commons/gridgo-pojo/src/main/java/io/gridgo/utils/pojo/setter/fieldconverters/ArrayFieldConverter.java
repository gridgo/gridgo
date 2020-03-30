package io.gridgo.utils.pojo.setter.fieldconverters;

import java.util.ArrayList;

import static io.gridgo.utils.ArrayUtils.toArray;
import static io.gridgo.utils.ArrayUtils.toPrimitiveArray;
import static io.gridgo.utils.PrimitiveUtils.getWrapperType;

import io.gridgo.utils.exception.UnsupportedTypeException;
import io.gridgo.utils.pojo.PojoFieldSignature;
import io.gridgo.utils.pojo.setter.data.GenericData;
import io.gridgo.utils.pojo.setter.data.SequenceData;

public class ArrayFieldConverter implements GenericDataConverter, FieldConverter<SequenceData> {

    private static final ArrayFieldConverter INSTANCE = new ArrayFieldConverter();

    public static ArrayFieldConverter getInstance() {
        return INSTANCE;
    }

    private ArrayFieldConverter() {
        // Nothing to do
    }

    @Override
    public Object convert(SequenceData array, PojoFieldSignature signature) {
        var componentType = signature.getComponentType();
        var typeToCheck = componentType.isPrimitive() ? getWrapperType(componentType) : componentType;
        var results = new ArrayList<Object>();
        for (GenericData element : array) {
            if (element.isPrimitive()) {
                results.add(fromPrimitive(element.asPrimitive(), typeToCheck));
            } else if (element.isKeyValue()) {
                results.add(fromKeyValue(element.asKeyValue(), componentType, signature.getElementSetterProxy()));
            } else if (element.isReference()) {
                var ref = fromReference(element.asReference());
                if (!typeToCheck.isInstance(ref)) {
                    throw new UnsupportedTypeException("Unknown element type: " + element.getClass());
                }
                results.add(ref);
            } else {
                throw new UnsupportedTypeException("Unknown element type: " + element.getClass());
            }
        }
        return componentType.isPrimitive() //
                ? toPrimitiveArray(results, componentType) //
                : toArray(componentType, results);
    }
}
