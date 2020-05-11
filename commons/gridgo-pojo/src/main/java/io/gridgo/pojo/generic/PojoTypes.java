package io.gridgo.pojo.generic;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.LinkedList;
import java.util.List;

public class PojoTypes {

    public static PojoType extractTypeInfo(Type type, Class<?> effectiveClass) {
        if (type instanceof Class<?>)
            return PojoType.builder().rawType((Class<?>) type).build();

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
                            return PojoType.builder().rawType((Class<?>) t).build();
                        } else {
                            System.out.println("holy shit: " + t);
                            return PojoType.builder().rawType(Object.class).build();
                        }
                    }
                }
            } else if (genericDeclaration instanceof Method) {
                return PojoType.builder().rawType(Object.class).build();
            }
            return null;
        }
        System.out.println("found unknown type: " + type.getClass());
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
