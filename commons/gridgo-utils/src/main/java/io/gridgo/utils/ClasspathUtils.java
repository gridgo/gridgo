package io.gridgo.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import lombok.NonNull;

public class ClasspathUtils {

    private static final FieldAnnotationsScanner FIELD_ANNOTATIONS_SCANNER = new FieldAnnotationsScanner();
    private static final MethodAnnotationsScanner METHOD_ANNOTATIONS_SCANNER = new MethodAnnotationsScanner();
    private static final TypeAnnotationsScanner TYPE_ANNOTATIONS_SCANNER = new TypeAnnotationsScanner();
    private static final SubTypesScanner SUBTYPE_ANNOTATIONS_SCANNER = new SubTypesScanner();

    public static final synchronized Reflections reflections(Object... params) {
        var list = new ArrayList<Object>(Arrays.asList(params));
        list.add(FIELD_ANNOTATIONS_SCANNER);
        list.add(METHOD_ANNOTATIONS_SCANNER);
        list.add(TYPE_ANNOTATIONS_SCANNER);
        list.add(SUBTYPE_ANNOTATIONS_SCANNER);
        return new Reflections(list.toArray());
    }

    public static final <A extends Annotation> void scanForAnnotatedTypes( //
            @NonNull String packageName, //
            @NonNull Class<A> annotationType, //
            @NonNull BiConsumer<Class<?>, A> typeConsumer, //
            ClassLoader... classLoaders) {

        var reflections = reflections(packageName, classLoaders);
        scanForAnnotatedTypes(reflections, annotationType, typeConsumer);
    }

    public static <A extends Annotation> void scanForAnnotatedTypes(Reflections reflections, Class<A> annotationType,
            BiConsumer<Class<?>, A> typeConsumer) {
        var types = reflections.getTypesAnnotatedWith(annotationType);
        types.forEach(clz -> typeConsumer.accept(clz, clz.getAnnotation(annotationType)));
    }

    public static final <A extends Annotation> void scanForAnnotatedMethods( //
            @NonNull String packageName, //
            @NonNull Class<A> annotationType, //
            @NonNull BiConsumer<Method, A> acceptor, //
            ClassLoader... classLoaders) {

        var reflections = reflections(packageName, classLoaders);
        scanForAnnotatedMethods(reflections, annotationType, acceptor);
    }

    public static <A extends Annotation> void scanForAnnotatedMethods(Reflections reflections, Class<A> annotationType,
            BiConsumer<Method, A> acceptor) {
        var methods = reflections.getMethodsAnnotatedWith(annotationType);
        methods.forEach(method -> acceptor.accept(method, method.getAnnotation(annotationType)));
    }

    public static final <A extends Annotation> void scanForAnnotatedFields( //
            @NonNull String packageName, //
            @NonNull Class<A> annotationType, //
            @NonNull BiConsumer<Field, A> acceptor, //
            ClassLoader... classLoaders) {

        var reflections = reflections(packageName, classLoaders);
        scanForAnnotatedFields(reflections, annotationType, acceptor);
    }

    public static <A extends Annotation> void scanForAnnotatedFields(Reflections reflections, Class<A> annotationType,
            BiConsumer<Field, A> acceptor) {
        var fields = reflections.getFieldsAnnotatedWith(annotationType);
        fields.forEach(field -> acceptor.accept(field, field.getAnnotation(annotationType)));
    }

    public static final <Type> void scanForSubTypes(@NonNull String packageName, //
            @NonNull Class<Type> superType, //
            @NonNull Consumer<Class<? extends Type>> acceptor, //
            ClassLoader... classLoaders) {

        var reflections = reflections(packageName, classLoaders);
        scanForSubTypes(reflections, superType, acceptor);
    }

    public static <Type> void scanForSubTypes(Reflections reflections, Class<Type> superType,
            Consumer<Class<? extends Type>> acceptor) {
        var types = reflections.getSubTypesOf(superType);
        types.forEach(acceptor);
    }
}
