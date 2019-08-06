package io.gridgo.bean.support;

import static io.gridgo.utils.ArrayUtils.foreachArray;
import static io.gridgo.utils.ArrayUtils.toArray;
import static io.gridgo.utils.ArrayUtils.toPrimitiveArray;
import static io.gridgo.utils.PrimitiveUtils.getWrapperType;
import static io.gridgo.utils.PrimitiveUtils.isPrimitive;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BReference;
import io.gridgo.bean.BType;
import io.gridgo.bean.BValue;
import io.gridgo.bean.exceptions.InvalidTypeException;
import io.gridgo.utils.PrimitiveUtils;
import io.gridgo.utils.exception.UnsupportedTypeException;
import io.gridgo.utils.pojo.PojoMethodSignature;
import io.gridgo.utils.pojo.PojoUtils;
import io.gridgo.utils.pojo.getter.PojoGetterProxy;
import io.gridgo.utils.pojo.setter.PojoSetterProxy;
import io.gridgo.utils.pojo.setter.ValueHolder;
import lombok.NonNull;

public class BElementPojoHelper {

    public static BElement anyToBElement(Object any) {
        return anyToBElement(any, null);
    }

    public static BElement anyToBElement(Object target, PojoGetterProxy proxy) {
        Class<?> type;
        if (target == null //
                || isPrimitive(type = target.getClass())) {
            return BValue.of(target);
        }

        if (type == Date.class //
                || type == java.sql.Date.class) {
            return BReference.of(target);
        }

        if (BElement.class.isInstance(target)) {
            return (BElement) target;
        }

        if (type.isArray()) {
            var list = BArray.ofEmpty();
            var _proxy = proxy;
            foreachArray(target, ele -> {
                list.add(anyToBElement(ele, _proxy));
            });
            return list;
        }

        if (Collection.class.isInstance(target)) {
            var it = ((Collection<?>) target).iterator();
            var list = BArray.ofEmpty();
            while (it.hasNext()) {
                list.add(anyToBElement(it.next(), proxy));
            }
            return list;
        }

        if (Map.class.isInstance(target)) {
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

    public static Object anyToJsonElement(Object any) {
        return anyToBElement(any, null);
    }

    public static <T> T bObjectToPojo(BObject src, @NonNull Class<T> type) {
        if (src == null) {
            return null;
        }
        var proxy = PojoUtils.getSetterProxy(type);
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
        fillToPojo(src, result, proxy);
        return result;
    }

    public static <T> void fillToPojo(BObject src, T result) {
        fillToPojo(src, result, PojoUtils.getSetterProxy(result.getClass()));
    }

    public static <T> void fillToPojo(BObject src, T result, PojoSetterProxy proxy) {
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

            BType valueType = value.getType();

            if (PrimitiveUtils.isPrimitive(signature.getFieldType())) {
                if (!value.isValue()) {
                    throw new InvalidTypeException("field '" + fieldName + "' expected BValue, but got: " + valueType);
                }
                return PrimitiveUtils.getValueFrom(signature.getFieldType(), value.asValue().getData());
            }

            if (signature.isSequenceType()) {
                if (!value.isArray()) {
                    throw new InvalidTypeException("Field '" + fieldName + "' expected BArray, but got: " + valueType);
                }
                return toSequence(value.asArray(), signature);
            }

            if (signature.isMapOrPojoType()) {
                if (value.isReference() && signature.isPojoType()) {
                    return value.asReference().getReference();
                }
                if (!value.isObject()) {
                    throw new InvalidTypeException("Field '" + fieldName + "' expected BObject, but got: " + valueType);
                }
                return toMapOrPojo(value.asObject(), signature);
            }

            return ValueHolder.NO_VALUE;
        });
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
                                coll.add(toPrimitiveArray(list, compType));
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
                    ? toPrimitiveArray(results, componentType) //
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
