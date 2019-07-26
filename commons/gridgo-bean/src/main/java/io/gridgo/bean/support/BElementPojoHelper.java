package io.gridgo.bean.support;

import static io.gridgo.utils.ArrayUtils.createPrimitiveArray;
import static io.gridgo.utils.ArrayUtils.toArray;
import static io.gridgo.utils.ArrayUtils.toPrimitiveTypeArray;
import static io.gridgo.utils.PrimitiveUtils.getWrapperType;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BReference;
import io.gridgo.bean.BValue;
import io.gridgo.bean.exceptions.InvalidTypeException;
import io.gridgo.utils.ArrayUtils;
import io.gridgo.utils.MapUtils;
import io.gridgo.utils.PrimitiveUtils;
import io.gridgo.utils.exception.UnsupportedTypeException;
import io.gridgo.utils.pojo.PojoMethodSignature;
import io.gridgo.utils.pojo.PojoUtils;
import io.gridgo.utils.pojo.getter.PojoGetterProxy;
import io.gridgo.utils.pojo.getter.PojoGetterRegistry;
import io.gridgo.utils.pojo.setter.PojoSetterProxy;
import io.gridgo.utils.pojo.setter.PojoSetterRegistry;
import io.gridgo.utils.pojo.setter.ValueHolder;
import lombok.NonNull;

public class BElementPojoHelper {

    private static final Set<Class<?>> IGNORE_TYPES = new HashSet<>(Arrays.asList(Date.class, java.util.Date.class));

    private static final PojoSetterRegistry SETTER_REGISTRY = PojoSetterRegistry.DEFAULT;
    private static final PojoGetterRegistry GETTER_REGISTRY = PojoGetterRegistry.DEFAULT;

    public static BElement anyToBElement(Object any) {
        return anyToBElement(any, null);
    }

    public static BElement anyToBElement(Object any, PojoGetterProxy proxy) {
        if (any == null) {
            return BValue.ofEmpty();
        }

        if (any instanceof BElement) {
            return (BElement) any;
        }

        var type = any.getClass();

        if (any instanceof Map) {
            var result = BObject.ofEmpty();
            for (Entry<?, ?> entry : ((Map<?, ?>) any).entrySet()) {
                result.put(entry.getKey().toString(), anyToBElement(entry.getValue(), null));
            }
            return result;
        } else if (ArrayUtils.isArrayOrCollection(type)) {
            var result = BArray.ofEmpty();
            ArrayUtils.foreach(any, ele -> result.add(anyToBElement(ele, null)));
            return result;
        } else if (PrimitiveUtils.isPrimitive(type)) {
            return BValue.of(any);
        }

        if (proxy == null) {
            proxy = PojoUtils.getGetterProxy(type);
        }

        var result = BObject.ofEmpty();
        proxy.walkThrough(any, (signature, value) -> {
            if (value != null && IGNORE_TYPES.contains(value.getClass())) {
                result.put(signature.getTransformedOrDefaultFieldName(), BReference.of(value));
            } else {
                result.put(signature.getTransformedOrDefaultFieldName(),
                        anyToBElement(value, signature.getGetterProxy()));
            }
        });
        return result;
    }

    public static Object anyToJsonElement(Object any) {
        return toJsonElement(any, null);
    }

    private static Object toJsonElement(Object any, PojoGetterProxy proxy) {

        if (any == null) {
            return null;
        }

        var type = any.getClass();
        if (PrimitiveUtils.isPrimitive(type)) {
            return any;
        }
        if (BElement.class.isAssignableFrom(type)) {
            return ((BElement) any).toJsonElement();
        }
        if (MapUtils.isMap(type)) {
            var result = new HashMap<String, Object>();
            for (Entry<?, ?> entry : ((Map<?, ?>) any).entrySet()) {
                result.put(entry.getKey().toString(), toJsonElement(entry.getValue(), null));
            }
            return result;
        }
        if (ArrayUtils.isArrayOrCollection(type)) {
            var result = new LinkedList<Object>();
            ArrayUtils.foreach(any, ele -> result.add(toJsonElement(ele, null)));
            return result;
        }
        if (proxy == null) {
            proxy = GETTER_REGISTRY.getGetterProxy(type);
        }

        var result = new HashMap<String, Object>();
        proxy.walkThrough(any, (signature, value) -> {
            String fieldName = signature.getTransformedOrDefaultFieldName();
            if (value != null && IGNORE_TYPES.contains(value.getClass())) {
                result.put(fieldName, BReference.of(value));
            } else {
                Object jsonElement = toJsonElement(value, signature.getGetterProxy());
                result.put(fieldName, jsonElement);
            }
        });
        return result;
    }

    public static <T> T bObjectToPojo(BObject src, @NonNull Class<T> type) {
        if (src == null) {
            return null;
        }
        var proxy = SETTER_REGISTRY.getSetterProxy(type);
        T result = bObjectToPojo(src, type, proxy);
        return result;
    }

    public static <T> T bObjectToPojo(BObject src, Class<T> type, PojoSetterProxy proxy) {
        T result = null;
        try {
            result = type.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot convert BObject to POJO, cannot create instance of: " + type.getName(),
                    e);
        }
        proxy.walkThrough(result, (signature) -> {
            var fieldName = signature.getFieldName();
            var transformedFieldName = signature.getTransformedFieldName();

            BElement value = transformedFieldName != null && !transformedFieldName.isBlank() //
                    ? src.getOrDefault(transformedFieldName, () -> src.getOrDefault(fieldName, () -> null)) //
                    : src.getOrDefault(fieldName, () -> null);

            if (value == null) {
                return ValueHolder.NO_VALUE;
            }

            if (value.isNullValue()) {
                if (signature.getFieldType().isPrimitive()) {
                    return ValueHolder.NO_VALUE;
                }
                return null;
            }

            if (PrimitiveUtils.isPrimitive(signature.getFieldType())) {
                if (!value.isValue()) {
                    throw new InvalidTypeException("Expected BValue, got: " + value.getType());
                }
                return PrimitiveUtils.getValueFrom(signature.getFieldType(), value.asValue().getData());
            }

            if (signature.isSequenceType()) {
                if (!value.isArray()) {
                    throw new InvalidTypeException("Expected BArray for field: " + signature.getFieldName() + ", type: "
                            + type + ", got: " + value);
                }
                return toSequence(value.asArray(), signature);
            }

            if (signature.isMapOrPojoType()) {
                if (value.isReference() && signature.isPojoType()) {
                    return value.asReference().getReference();
                }
                if (!value.isObject()) {
                    throw new InvalidTypeException("Expected BObject, got: " + value.getType());
                }
                return toMapOrPojo(value.asObject(), signature);
            }

            return ValueHolder.NO_VALUE;
        });
        return result;
    }

    private static Object toSequence(BArray array, PojoMethodSignature signature) {
        if (signature.isCollectionType()) {
            Collection<Object> coll;
            if (signature.isSetType()) {
                coll = new HashSet<>();
            } else {
                coll = new LinkedList<>();
            }

            Class<?>[] genericTypes = signature.getGenericTypes();
            Class<?> resultElementType = (genericTypes.length == 0) ? Object.class : genericTypes[0];

            if (resultElementType == Object.class) {
                coll.addAll(array.toList());
            } else {
                for (BElement bElement : array) {
                    if (bElement.isNullValue()) {
                        coll.add(null);
                    } else if (bElement.isObject()) {
                        coll.add(bObjectToPojo(bElement.asObject(), resultElementType,
                                signature.getElementSetterProxy()));
                    } else if (bElement.isArray()) {
                        List<Object> list = bElement.asArray().toList();
                        if (resultElementType.isArray()) {
                            var compType = resultElementType.getComponentType();
                            if (compType.isPrimitive()) {
                                coll.add(createPrimitiveArray(compType, list));
                            } else {
                                coll.add(toArray(compType, list));
                            }
                        } else {
                            coll.add(list);
                        }
                    } else if (bElement.isValue()) {
                        coll.add(bElement.asValue().getData());
                    } else if (bElement.isReference()) {
                        coll.add(bElement.asReference().getReference());
                    } else {
                        throw new UnsupportedTypeException("Unknown element type: " + bElement.getClass());
                    }
                }
            }

            return coll;
        } else if (signature.isArrayType()) {
            Class<?> componentType = signature.getComponentType();
            var results = new ArrayList<Object>();
            for (BElement element : array) {
                if (element.isValue()) {
                    if (element.isNullValue())
                        results.add(null);
                    else
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
                    ? toPrimitiveTypeArray(componentType, results) //
                    : toArray(componentType, results);
        }

        throw new InvalidTypeException("Cannot convert BArray to incompatible type: " + signature.getFieldType());
    }

    private static Object toMapOrPojo(BObject valueObj, PojoMethodSignature signature) {
        if (signature.isMapType()) {
            Class<?>[] genericTypes = signature.getGenericTypes();
            Class<?> valueType = genericTypes.length > 1 ? genericTypes[1] : Object.class;
            var map = new HashMap<String, Object>();
            for (Entry<String, BElement> entry : valueObj.entrySet()) {
                Object entryValue;
                if (entry.getValue().isValue()) {
                    entryValue = entry.getValue().asValue().getData();
                } else if (entry.getValue().isArray()) {
                    entryValue = entry.getValue().asArray().toList();
                } else if (entry.getValue().isObject()) {
                    if (valueType == Object.class) {
                        entryValue = entry.getValue().asObject().toMap();
                    } else {
                        entryValue = bObjectToPojo(entry.getValue().asObject(), valueType,
                                signature.getElementSetterProxy());
                    }
                } else if (entry.getValue().isReference()) {
                    entryValue = entry.getValue().asReference().getReference();
                } else {
                    throw new UnsupportedTypeException("Unknown entry value type: " + entry.getValue().getClass());
                }
                map.put(entry.getKey(), entryValue);
            }
            return map;
        } else if (signature.isPojoType()) {
            return bObjectToPojo(valueObj, signature.getFieldType(), signature.getSetterProxy());
        }

        throw new InvalidTypeException("Cannot convert BObject to incompatible type: " + signature.getFieldType());
    }
}
