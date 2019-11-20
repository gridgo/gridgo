package io.gridgo.bean.support;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static io.gridgo.utils.ArrayUtils.foreachArray;
import static io.gridgo.utils.ArrayUtils.toArray;
import static io.gridgo.utils.ArrayUtils.toPrimitiveArray;
import static io.gridgo.utils.PrimitiveUtils.getWrapperType;
import static io.gridgo.utils.PrimitiveUtils.isPrimitive;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BReference;
import io.gridgo.bean.BType;
import io.gridgo.bean.BValue;
import io.gridgo.bean.exceptions.InvalidTypeException;
import io.gridgo.bean.exceptions.InvalidValueException;
import io.gridgo.utils.PrimitiveUtils;
import io.gridgo.utils.exception.UnsupportedTypeException;
import io.gridgo.utils.pojo.PojoMethodSignature;
import io.gridgo.utils.pojo.PojoUtils;
import io.gridgo.utils.pojo.getter.PojoGetterProxy;
import io.gridgo.utils.pojo.setter.PojoSetterProxy;
import io.gridgo.utils.pojo.setter.ValueHolder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BElementPojoHelper {

    public static BElement anyToBElement(Object any) {
        return anyToBElement(any, null);
    }

    public static BElement anyToBElement(Object target, PojoGetterProxy proxy) {
        if (target == null)
            return BValue.of(null);

        Class<?> type = target.getClass();

        if (isPrimitive(type)) {
            return BValue.of(target);
        }

        if (type == Date.class || type == java.sql.Date.class) {
            return BReference.of(target);
        }

        if (BElement.class.isInstance(target)) {
            return (BElement) target;
        }

        if (type.isArray()) {
            return arrayToBElement(target, proxy);
        }

        if (Collection.class.isInstance(target)) {
            return collectionToBElement(target, proxy);
        }

        if (Map.class.isInstance(target)) {
            return mapToBElement(target, proxy);
        }

        proxy = proxy == null ? PojoUtils.getGetterProxy(type) : proxy;

        var result = BObject.ofEmpty();
        proxy.walkThrough(target, (signature, value) -> {
            String fieldName = signature.getTransformedOrDefaultFieldName();
            PojoGetterProxy elementGetterProxy = signature.getElementGetterProxy();
            BElement entryValue = anyToBElement(value,
                    elementGetterProxy == null ? signature.getGetterProxy() : elementGetterProxy);
            result.put(fieldName, entryValue);
        });
        return result;
    }

    private static BElement mapToBElement(Object target, PojoGetterProxy proxy) {
        var result = BObject.ofEmpty();
        var map = (Map<?, ?>) target;
        var it = map.entrySet().iterator();
        while (it.hasNext()) {
            var entry = it.next();
            var key = entry.getKey();
            var value = entry.getValue();
            result.put(key.toString(), anyToBElement(value, proxy));
        }
        return result;
    }

    private static BElement collectionToBElement(Object target, PojoGetterProxy proxy) {
        var it = ((Collection<?>) target).iterator();
        var list = BArray.ofEmpty();
        while (it.hasNext()) {
            list.add(anyToBElement(it.next(), proxy));
        }
        return list;
    }

    private static BElement arrayToBElement(Object target, PojoGetterProxy proxy) {
        var list = BArray.ofEmpty();
        var _proxy = proxy;
        foreachArray(target, ele -> {
            list.add(anyToBElement(ele, _proxy));
        });
        return list;
    }

    public static <T> T bObjectToPojo(BObject src, @NonNull Class<T> type) {
        if (src == null)
            return null;
        var proxy = PojoUtils.getSetterProxy(type);
        return bObjectToPojo(src, type, proxy);
    }

    public static <T> T bObjectToPojo(BObject src, Class<T> type, PojoSetterProxy proxy) {
        T result = null;
        try {
            result = type.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Convert BObject to POJO errored, cannot create instance of: " + type.getName(),
                    e);
        }
        fillToPojo(src, result, proxy);
        return result;
    }

    public static <T> void fillToPojo(BObject src, T result) {
        fillToPojo(src, result, PojoUtils.getSetterProxy(result.getClass()));
    }

    public static <T> void fillToPojo(BObject src, T result, PojoSetterProxy proxy) {
        proxy.walkThrough(result, signature -> doFillPojo(src, signature));
    }

    @SuppressWarnings("unchecked")
    private static Object doFillPojo(BObject src, PojoMethodSignature signature) {
        var fieldName = signature.getFieldName();
        var transformedFieldName = signature.getTransformedFieldName();

        BElement value = transformedFieldName != null && !transformedFieldName.isBlank() //
                ? src.getOrDefault(transformedFieldName, () -> src.getOrDefault(fieldName, () -> null)) //
                : src.getOrDefault(fieldName, () -> null);

        if (signature.getValueTranslator() != null && signature.getValueTranslator().translatable(value))
            return signature.getValueTranslator().translate(value);

        if (value == null)
            return ValueHolder.NO_VALUE;

        if (signature.getFieldType() == BElement.class)
            return value;

        Class<?> fieldType = signature.getFieldType();
        if (value.isNullValue()) {
            if (fieldType.isPrimitive()) {
                return ValueHolder.NO_VALUE;
            }
            return null;
        }

        BType valueType = value.getType();

        if (signature.isExtPrimitive()) {
            if (!value.isValue()) {
                throw new InvalidTypeException("field '" + fieldName + "' expected BValue, but got: " + valueType);
            }
            if (BValue.class.isAssignableFrom(fieldType))
                return value.asValue();
            var data = value.asValue().getData();
            try {
                return PrimitiveUtils.getValueFrom(fieldType, data);
            } catch (Exception e) {
                throw new InvalidValueException(
                        "Invalid value for field '" + fieldName + "', expected type: " + fieldType + ", got: " + data,
                        e);
            }
        }

        if (signature.isSequenceType()) {
            if (!value.isArray()) {
                throw new InvalidTypeException("Field '" + fieldName + "' expected BArray, but got: " + valueType);
            }
            if (BArray.class.isAssignableFrom(fieldType))
                return value.asArray();
            return toSequence(value.asArray(), signature);
        }

        if (signature.isMapOrPojoType()) {
            if (value.isReference() && signature.isPojoType()) {
                return value.asReference().getReference();
            }
            if (!value.isObject()) {
                throw new InvalidTypeException("Field '" + fieldName + "' expected BObject, but got: " + valueType);
            }
            if (BObject.class.isAssignableFrom(fieldType))
                return value.asObject();
            return toMapOrPojo(value.asObject(), signature);
        }

        return ValueHolder.NO_VALUE;
    }

    private static Object toSequence(BArray array, PojoMethodSignature signature) {
        if (signature.isCollectionType()) {
            return collectionToSequence(array, signature);
        }
        if (signature.isArrayType()) {
            return arrayToSequence(array, signature);
        }
        throw new InvalidTypeException("Cannot convert BArray to incompatible type: " + signature.getFieldType());
    }

    private static Object arrayToSequence(BArray array, PojoMethodSignature signature) {
        Class<?> componentType = signature.getComponentType();
        var results = new ArrayList<Object>();
        for (BElement element : array) {
            if (element.isValue()) {
                results.add(element.asValue().getData());
            } else if (element.isObject()) {
                results.add(bObjectToPojo(element.asObject(), componentType, signature.getElementSetterProxy()));
            } else if (element.isReference()) {
                BReference ref = element.asReference();
                Class<?> typeToCheck = componentType.isPrimitive() ? getWrapperType(componentType) : componentType;
                if (ref.referenceInstanceOf(typeToCheck)) {
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

    private static Object collectionToSequence(BArray array, PojoMethodSignature signature) {
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
            for (BElement bElement : array) {
                if (bElement == null || bElement.isNullValue()) {
                    if (!signature.isSetType())
                        coll.add(null);
                    else
                        log.warn("got null value for field {}, target is a set which doesn't allow null, ignored",
                                signature.getFieldName());
                } else if (bElement.isObject()) {
                    coll.add(bObjectToPojo(bElement.asObject(), resultElementType, signature.getElementSetterProxy()));
                } else if (bElement.isArray()) {
                    List<Object> list = bElement.asArray().toList();
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
                } else if (bElement.isValue()) {
                    coll.add(bElement.asValue().getDataAs(resultElementType));
                } else if (bElement.isReference()) {
                    coll.add(bElement.asReference().getReference());
                } else {
                    throw new UnsupportedTypeException("Unknown element type: " + bElement.getClass());
                }
            }
        }

        return coll;
    }

    private static Object toMapOrPojo(BObject valueObj, PojoMethodSignature signature) {
        if (signature.isMapType())
            return bObjectToMap(valueObj, signature);
        if (signature.isPojoType())
            return bObjectToPojo(valueObj, signature.getFieldType(), signature.getSetterProxy());
        throw new InvalidTypeException("Cannot convert BObject to incompatible type: " + signature.getFieldType());
    }

    private static Object bObjectToMap(BObject valueObj, PojoMethodSignature signature) {
        var genericTypes = signature.getGenericTypes();
        var valueType = (genericTypes != null && genericTypes.length > 1) ? genericTypes[1] : Object.class;
        var map = new HashMap<String, Object>();
        for (Entry<String, BElement> entry : valueObj.entrySet()) {
            var entryValue = convertBElementToType(signature, valueType, entry.getValue());
            map.put(entry.getKey(), entryValue);
        }
        return map;
    }

    private static Object convertBElementToType(PojoMethodSignature signature, Class<?> valueType,
            BElement theEntryValue) {
        if (valueType == Object.class)
            return theEntryValue;
        if (theEntryValue.isValue())
            return theEntryValue.asValue().getDataAs(valueType);
        if (theEntryValue.isArray() || theEntryValue.isReference())
            return theEntryValue.getInnerValue();
        if (theEntryValue.isObject()) {
            if (valueType == Object.class)
                return theEntryValue.getInnerValue();
            return bObjectToPojo(theEntryValue.asObject(), valueType, signature.getElementSetterProxy());
        }
        throw new UnsupportedTypeException("Unknown entry value type: " + theEntryValue.getClass());
    }
}
