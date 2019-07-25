package io.gridgo.bean.support;

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
import io.gridgo.utils.PrimitiveUtils;
import io.gridgo.utils.exception.UnsupportedTypeException;
import io.gridgo.utils.pojo.PojoMethodSignature;
import io.gridgo.utils.pojo.getter.PojoGetterRegistry;
import io.gridgo.utils.pojo.setter.PojoSetterRegistry;
import io.gridgo.utils.pojo.setter.ValueHolder;
import lombok.NonNull;

public class BElementPojoHelper {

    private static final Set<Class<?>> IGNORE_TYPES = new HashSet<>(Arrays.asList(Date.class, java.util.Date.class));

    private static final PojoSetterRegistry SETTER_REGISTRY = PojoSetterRegistry.getInstance();
    private static final PojoGetterRegistry GETTER_REGISTRY = PojoGetterRegistry.getInstance();

    public static BElement anyToBElement(Object any) {
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
                result.put(entry.getKey().toString(), anyToBElement(entry.getValue()));
            }
            return result;
        } else if (ArrayUtils.isArrayOrCollection(type)) {
            var result = BArray.ofEmpty();
            ArrayUtils.foreach(any, ele -> result.add(anyToBElement(ele)));
            return result;
        } else if (PrimitiveUtils.isPrimitive(type)) {
            return BValue.of(any);
        }

        var result = BObject.ofEmpty();
        GETTER_REGISTRY.getGetterProxy(type).walkThrough(any, (signature, value) -> {
            if (value != null && IGNORE_TYPES.contains(value.getClass())) {
                result.put(signature.getTransformedOrDefaultFieldName(), BReference.of(value));
            } else {
                result.put(signature.getTransformedOrDefaultFieldName(), anyToBElement(value));
            }
        });
        return result;
    }

    public static Object pojoToJsonElement(Object any) {
        if (any == null) {
            return null;
        }

        if (any instanceof BElement) {
            return ((BElement) any).toJsonElement();
        }

        var type = any.getClass();
        if (any instanceof Map) {
            var result = new HashMap<String, Object>();
            for (Entry<?, ?> entry : ((Map<?, ?>) any).entrySet()) {
                result.put(entry.getKey().toString(), pojoToJsonElement(entry.getValue()));
            }
            return result;
        } else if (ArrayUtils.isArrayOrCollection(type)) {
            var result = new LinkedList<Object>();
            ArrayUtils.foreach(any, ele -> result.add(pojoToJsonElement(ele)));
            return result;
        } else if (PrimitiveUtils.isPrimitive(type)) {
            return any;
        }

        var result = new HashMap<String, Object>();
        GETTER_REGISTRY.getGetterProxy(type).walkThrough(any, (signature, value) -> {
            if (value != null && IGNORE_TYPES.contains(value.getClass())) {
                result.put(signature.getTransformedOrDefaultFieldName(), BReference.of(value));
            } else {
                result.put(signature.getTransformedOrDefaultFieldName(), pojoToJsonElement(value));
            }
        });
        return result;
    }

    public static <T> T toPojo(BObject src, @NonNull Class<T> type) {
        if (src == null) {
            return null;
        }

        try {
            T result = type.getConstructor().newInstance();
            var proxy = SETTER_REGISTRY.getSetterProxy(type);
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
                    return value.asValue().getData();
                }

                if (signature.isSequenceType()) {
                    if (!value.isArray()) {
                        throw new InvalidTypeException("Expected BArray, got: " + value.getType());
                    }
                    return toSequence(value.asArray(), signature);
                }

                if (signature.isKeyValueType()) {
                    if (value.isReference() && signature.isPojoType()) {
                        return value.asReference().getReference();
                    }
                    if (!value.isObject()) {
                        throw new InvalidTypeException("Expected BObject, got: " + value.getType());
                    }
                    return bObjectToKeyValue(value.asObject(), signature);
                }

                return ValueHolder.NO_VALUE;
            });
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Cannot convert BObject to POJO", e);
        }
    }

    private static Object toSequence(BArray array, PojoMethodSignature signature) {
        if (signature.isCollectionType()) {
            Collection<Object> collection;
            if (signature.isSetType()) {
                collection = new HashSet<>();
            } else {
                collection = new LinkedList<>();
            }

            Class<?>[] genericTypes = signature.getGenericTypes();
            Class<?> elementType = (genericTypes.length == 0) ? Object.class : genericTypes[0];
            if (elementType == Object.class) {
                collection.addAll(array.toList());
            } else {
                for (BElement element : array) {
                    if (element.isNullValue()) {
                        collection.add(null);
                    } else if (element.isObject()) {
                        collection.add(toPojo(element.asObject(), elementType));
                    } else if (element.isArray()) {
                        collection.add(element.asArray().toList());
                    } else if (element.isValue()) {
                        collection.add(element.asValue().getData());
                    } else if (element.isReference()) {
                        collection.add(element.asReference().getReference());
                    } else {
                        throw new UnsupportedTypeException("Unknown element type: " + element.getClass());
                    }
                }
            }

            return collection;
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
                    results.add(toPojo(element.asObject(), componentType));
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

    private static Object bObjectToKeyValue(BObject valueObj, PojoMethodSignature signature) {
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
                        entryValue = toPojo(entry.getValue().asObject(), valueType);
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
            return toPojo(valueObj, signature.getFieldType());
        }

        throw new InvalidTypeException("Cannot convert BObject to incompatible type: " + signature.getFieldType());
    }
}
