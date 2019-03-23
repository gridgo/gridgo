package io.gridgo.boot.support.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AnnotationUtils {

    public static List<Method> findAllDeclaredMethodsWithAnnotation(Class<?> clazz,
            Class<? extends Annotation> annotation) {
        return findAllMethodsWithAnnotation(clazz, annotation, true);
    }

    public static List<Method> findAllMethodsWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotation) {
        return findAllMethodsWithAnnotation(clazz, annotation, false);
    }

    public static List<Method> findAllMethodsWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotation,
            boolean declared) {
        var methods = new ArrayList<Method>();
        while (clazz != Object.class) { // need to iterated thought hierarchy in order to retrieve methods from above
                                        // the current instance
            // iterate though the list of methods declared in the class represented by klass
            // variable, and add those annotated with the specified annotation
            var allMethods = Arrays.stream(declared ? clazz.getDeclaredMethods() : clazz.getMethods())
                                   .filter(method -> method.isAnnotationPresent(annotation))
                                   .collect(Collectors.toList());
            methods.addAll(allMethods);
            // move to the upper class in the hierarchy in search for more methods
            clazz = clazz.getSuperclass();
        }
        return methods;
    }

    public static List<Field> findAllFieldsWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotation) {
        return Arrays.stream(clazz.getDeclaredFields()) //
                     .filter(field -> field.isAnnotationPresent(annotation)) //
                     .collect(Collectors.toList());
    }
}
