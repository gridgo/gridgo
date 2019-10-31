package io.gridgo.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;

import lombok.NonNull;

public class ClasspathUtils {

    private static final FieldAnnotationsScanner FIELD_ANNOTATIONS_SCANNER = new FieldAnnotationsScanner();
    private static final MethodAnnotationsScanner METHOD_ANNOTATIONS_SCANNER = new MethodAnnotationsScanner();

    public static final synchronized Reflections reflections(Object... params) {
        return new Reflections(params);
    }

    public static final <A extends Annotation> void scanForAnnotatedTypes( //
            @NonNull String packageName, //
            @NonNull Class<A> annotationType, //
            @NonNull BiConsumer<Class<?>, A> typeConsumer, //
            ClassLoader... classLoaders) {

        var reflections = reflections(packageName, classLoaders);
        var types = reflections.getTypesAnnotatedWith(annotationType);
        types.forEach(clz -> typeConsumer.accept(clz, clz.getAnnotation(annotationType)));
    }

    public static final <A extends Annotation> void scanForAnnotatedMethods( //
            @NonNull String packageName, //
            @NonNull Class<A> annotationType, //
            @NonNull BiConsumer<Method, A> acceptor, //
            ClassLoader... classLoaders) {

        var reflections = reflections(packageName, METHOD_ANNOTATIONS_SCANNER, classLoaders);
        var methods = reflections.getMethodsAnnotatedWith(annotationType);
        methods.forEach(method -> acceptor.accept(method, method.getAnnotation(annotationType)));
    }

    public static final <A extends Annotation> void scanForAnnotatedFields( //
            @NonNull String packageName, //
            @NonNull Class<A> annotationType, //
            @NonNull BiConsumer<Field, A> acceptor, //
            ClassLoader... classLoaders) {

        var reflections = reflections(packageName, FIELD_ANNOTATIONS_SCANNER, classLoaders);
        var fields = reflections.getFieldsAnnotatedWith(annotationType);
        fields.forEach(field -> acceptor.accept(field, field.getAnnotation(annotationType)));
    }

    public static final <Type> void scanForSubTypes(@NonNull String packageName, //
            @NonNull Class<Type> superType, //
            @NonNull Consumer<Class<? extends Type>> acceptor, //
            ClassLoader... classLoaders) {

        var reflections = reflections(packageName, classLoaders);
        var types = reflections.getSubTypesOf(superType);
        types.forEach(acceptor);
    }
}
