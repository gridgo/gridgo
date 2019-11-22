package io.gridgo.utils.pojo.setter;

import static io.gridgo.utils.ArrayUtils.toArray;
import static io.gridgo.utils.ArrayUtils.toPrimitiveArray;
import static io.gridgo.utils.PrimitiveUtils.getWrapperType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import io.gridgo.utils.PrimitiveUtils;
import io.gridgo.utils.exception.UnsupportedTypeException;
import io.gridgo.utils.pojo.PojoMethodSignature;
import io.gridgo.utils.pojo.exception.PojoProxyException;
import io.gridgo.utils.pojo.setter.data.GenericData;
import io.gridgo.utils.pojo.setter.data.KeyValueData;
import io.gridgo.utils.pojo.setter.data.SequenceData;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PojoSetter {

    public static PojoSetter of(Object target, PojoSetterProxy proxy) {
        return new PojoSetter(target, proxy);
    }

    public static PojoSetter of(Object target) {
        return of(target, PojoSetterRegistry.DEFAULT.getSetterProxy(target.getClass()));
    }

    public static PojoSetter ofType(Class<?> targetType, PojoSetterProxy proxy) {
        try {
            var target = targetType.getConstructor().newInstance();
            return of(target, proxy);
        } catch (Exception e) {
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

    @SuppressWarnings("unchecked")
    private Object onField(PojoMethodSignature signature) {
        var fieldName = signature.getFieldName();
        var transformedFieldName = signature.getTransformedFieldName();

        var value = transformedFieldName != null //
                ? source.getOrTake(transformedFieldName, () -> source.getOrDefault(fieldName, null)) //
                : source.getOrDefault(fieldName, null);

        var valueTranslator = signature.getValueTranslator();
        if (valueTranslator != null && valueTranslator.translatable(value))
            return valueTranslator.translate(value, signature);

        if (value == null)
            return ValueHolder.NO_VALUE;

        var fieldType = signature.getFieldType();
        if (value.isPrimitive() && value.asPrimitive().getData() == null) {
            if (fieldType.isPrimitive())
                return ValueHolder.NO_VALUE;
            return null;
        }

        if (signature.isExtPrimitive()) {
            if (!value.isPrimitive())
                throw new PojoProxyException("field '" + fieldName + "' expected value data");

            var data = value.asPrimitive().getData();
            if (data == null)
                return null;

            if (signature.getFieldType().isAssignableFrom(data.getClass()))
                return data;

            try {
                return PrimitiveUtils.getValueFrom(fieldType, data);
            } catch (Exception e) {
                throw new PojoProxyException(
                        "Invalid value for field '" + fieldName + "', expected type: " + fieldType + ", got: " + data,
                        e);
            }
        }

        if (signature.isSequenceType()) {
            if (!value.isSequence()) {
                throw new PojoProxyException("Field '" + fieldName + "' expected sequence");
            }
            return toSequence(value.asSequence(), signature);
        }

        if (signature.isMapOrPojoType()) {
            if (value.isReference() && signature.isPojoType())
                return value.asReference().getReference();

            if (!value.isKeyValue())
                throw new PojoProxyException("Field '" + fieldName + "' expected key-value");

            return toMapOrPojo(value.asKeyValue(), signature);
        }

        return ValueHolder.NO_VALUE;
    }

    private Object toSequence(SequenceData array, PojoMethodSignature signature) {
        if (signature.isCollectionType()) {
            return collectionToSequence(array, signature);
        }
        if (signature.isArrayType()) {
            return arrayToSequence(array, signature);
        }
        throw new PojoProxyException("Cannot convert BArray to incompatible type: " + signature.getFieldType());
    }

    private Object arrayToSequence(SequenceData array, PojoMethodSignature signature) {
        var componentType = signature.getComponentType();
        var results = new ArrayList<Object>();
        for (GenericData element : array) {
            if (element.isPrimitive()) {
                results.add(element.asPrimitive().getData());
            } else if (element.isKeyValue()) {
                results.add(ofType(componentType, signature.getElementSetterProxy()).from(element.asKeyValue()));
            } else if (element.isReference()) {
                var ref = element.asReference();
                var typeToCheck = componentType.isPrimitive() ? getWrapperType(componentType) : componentType;
                if (typeToCheck.isInstance(ref.getReference())) {
                    results.add(ref.getReference());
                } else {
                    throw new UnsupportedTypeException("Unknown element type: " + element.getClass());
                }
            } else {
                throw new UnsupportedTypeException("Unknown element type: " + element.getClass());
            }
        }
        return componentType.isPrimitive() //
                ? toPrimitiveArray(results, componentType) //
                : toArray(componentType, results);
    }

    @SuppressWarnings("unchecked")
    private Object collectionToSequence(SequenceData array, PojoMethodSignature signature) {
        Collection<Object> coll;
        if (signature.isSetType()) {
            coll = new HashSet<>();
        } else {
            coll = new LinkedList<>();
        }

        Class<?>[] genericTypes = signature.getGenericTypes();
        Class<?> resultElementType = (genericTypes == null || genericTypes.length == 0) //
                ? Object.class //
                : genericTypes[0];

        if (resultElementType == Object.class) {
            coll.addAll(array.toList());
        } else {
            for (GenericData bElement : array) {
                if (bElement == null || bElement.isNull()) {
                    if (!signature.isSetType())
                        coll.add(null);
                    else
                        log.warn("got null value for field {}, target is a set which doesn't allow null, ignored",
                                signature.getFieldName());
                } else if (bElement.isKeyValue()) {
                    coll.add(ofType(resultElementType, signature.getElementSetterProxy()) //
                            .from(bElement.asKeyValue()) //
                            .fill());
                } else if (bElement.isSequence()) {
                    List<Object> list = bElement.asSequence().toList();
                    if (resultElementType.isArray()) {
                        var compType = resultElementType.getComponentType();
                        if (compType.isPrimitive()) {
                            coll.add(toPrimitiveArray(list, compType));
                        } else {
                            coll.add(toArray(compType, list));
                        }
                    } else {
                        coll.add(list);
                    }
                } else if (bElement.isPrimitive()) {
                    coll.add(bElement.asPrimitive().getDataAs(resultElementType));
                } else if (bElement.isReference()) {
                    coll.add(bElement.asReference().getReference());
                } else {
                    throw new UnsupportedTypeException("Unknown element type: " + bElement.getClass());
                }
            }
        }

        return coll;
    }

    private Object toMapOrPojo(KeyValueData keyValueData, PojoMethodSignature signature) {
        if (signature.isMapType())
            return keyValueToMap(keyValueData, signature);
        if (signature.isPojoType())
            return ofType(signature.getFieldType(), signature.getSetterProxy()).from(keyValueData).fill();
        throw new PojoProxyException(
                "Cannot convert non key-value data to incompatible type: " + signature.getFieldType());
    }

    private Object keyValueToMap(KeyValueData keyValueData, PojoMethodSignature signature) {
        var map = new HashMap<String, Object>();
        for (var entry : keyValueData) {
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
            return ofType(valueType, signature.getElementSetterProxy()).from(data.asKeyValue()).fill();

        throw new UnsupportedTypeException("Unknown entry value type: " + data.getClass());
    }
}
