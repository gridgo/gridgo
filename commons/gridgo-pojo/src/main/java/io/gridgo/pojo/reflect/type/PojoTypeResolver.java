package io.gridgo.pojo.reflect.type;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.LinkedList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PojoTypeResolver {

    public static PojoType extractFieldTypeInfo(Field field) {
        return extractFieldTypeInfo(field, null);
    }

    public static PojoType extractFieldTypeInfo(Field field, Class<?> effectiveClass) {
        effectiveClass = effectiveClass != null ? effectiveClass : field.getDeclaringClass();
        return extractTypeInfo(field.getGenericType(), effectiveClass);
    }

    public static PojoType extractReturnTypeInfo(Method method) {
        return extractReturnTypeInfo(method, null);
    }

    public static PojoType extractReturnTypeInfo(Method method, Class<?> effectiveClass) {
        effectiveClass = effectiveClass != null ? effectiveClass : method.getDeclaringClass();
        return extractTypeInfo(method.getGenericReturnType(), effectiveClass);
    }

    public static PojoType extractParamTypeInfo(Method method, int paramNo) {
        return extractParamTypeInfo(method, paramNo, null);
    }

    public static PojoType extractParamTypeInfo(Method method, int paramNo, Class<?> effectiveClass) {
        effectiveClass = effectiveClass != null ? effectiveClass : method.getDeclaringClass();
        return extractTypeInfo(method.getGenericParameterTypes()[paramNo], effectiveClass);
    }

    public static PojoType extractFirstParamTypeInfo(Method method) {
        return extractFirstParamTypeInfo(method, null);
    }

    public static PojoType extractFirstParamTypeInfo(Method method, Class<?> effectiveClass) {
        return extractParamTypeInfo(method, 0, effectiveClass);
    }

    public static PojoType extractTypeInfo(Type type, Class<?> effectiveClass) {

        if (type instanceof Class<?>) {
            var typeCls = (Class<?>) type;
            if (typeCls.isArray()) {
                var componentType = typeCls.getComponentType();
                var typeInfo = extractTypeInfo(componentType, effectiveClass);
                return PojoArrayType.builder().componentType(typeInfo).build();
            }
            return PojoSimpleType.builder().type(typeCls).build();
        }

        if (type instanceof ParameterizedType)
            return PojoParameterizedType.builder() //
                    .rawType((Class<?>) ((ParameterizedType) type).getRawType()) //
                    .actualTypeArguments(extractGenericTypes((ParameterizedType) type, effectiveClass)) //
                    .build();

        if (type instanceof TypeVariable<?>) {
            var typeVariable = (TypeVariable<?>) type;
            var genericDeclaration = typeVariable.getGenericDeclaration();
            if (genericDeclaration instanceof Class) {
                if (((Class<?>) genericDeclaration).isAssignableFrom(effectiveClass)) {
                    Type t = effectiveClass.getGenericSuperclass();
                    while (true) {
                        if (t instanceof ParameterizedType) {
                            var pType = (ParameterizedType) t;
                            if (pType.getRawType() == genericDeclaration) {
                                var vars = genericDeclaration.getTypeParameters();
                                var args = pType.getActualTypeArguments();
                                for (int i = 0; i < vars.length; i++) {
                                    if (vars[i].getName() == typeVariable.getName()) {
                                        var arg = args[i];
                                        return extractTypeInfo(arg, effectiveClass);
                                    }
                                }
                                return null;
                            }
                            t = ((Class<?>) pType.getRawType()).getGenericSuperclass();
                        } else if (t instanceof Class) {
                            return extractTypeInfo((Class<?>) t, effectiveClass);
                        } else {
                            return PojoSimpleType.OBJECT;
                        }
                    }
                }
            } else if (genericDeclaration instanceof Method) {
                return PojoSimpleType.OBJECT;
            }
        }

        if (type instanceof GenericArrayType) {
            var genericArrayType = (GenericArrayType) type;
            var componentType = genericArrayType.getGenericComponentType();
            return PojoArrayType.builder() //
                    .componentType(extractTypeInfo(componentType, effectiveClass)) //
                    .build();
        }

        log.warn("found unknown type: " + type.getClass() + ": " + type);
        return null;
    }

    private static List<PojoType> extractGenericTypes(ParameterizedType type, Class<?> effectiveClass) {
        var results = new LinkedList<PojoType>();
        var actualTypeArgs = type.getActualTypeArguments();
        for (var actualType : actualTypeArgs)
            results.add(extractTypeInfo(actualType, effectiveClass));
        return results;
    }
}
