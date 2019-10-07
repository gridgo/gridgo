package io.gridgo.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.function.BiConsumer;

import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;

import lombok.NonNull;

public class ClasspathUtils {

    private static final Object typeScannerLock = new Object();
    private static final Object methodScannerLock = new Object();
    private static final Object fieldScannerLock = new Object();

    private static final FieldAnnotationsScanner FIELD_ANNOTATIONS_SCANNER = new FieldAnnotationsScanner();
    private static final MethodAnnotationsScanner METHOD_ANNOTATIONS_SCANNER = new MethodAnnotationsScanner();

    public static final <A extends Annotation> void scanForAnnotatedTypes( //
            @NonNull String packageName, //
            @NonNull Class<A> annotationType, //
            @NonNull BiConsumer<Class<?>, A> typeConsumer, //
            ClassLoader... classLoaders) {

        Reflections reflections;
        synchronized (typeScannerLock) {
            reflections = new Reflections(packageName, classLoaders);
        }

        Set<Class<?>> types = reflections.getTypesAnnotatedWith(annotationType);
        types.forEach(clz -> typeConsumer.accept(clz, clz.getAnnotation(annotationType)));
    }

    public static final <A extends Annotation> void scanForAnnotatedMethods( //
            @NonNull String packageName, //
            @NonNull Class<A> annotationType, //
            @NonNull BiConsumer<Method, A> acceptor, //
            ClassLoader... classLoaders) {

        Reflections reflections;
        synchronized (methodScannerLock) {
            reflections = new Reflections(packageName, METHOD_ANNOTATIONS_SCANNER, classLoaders);
        }
        Set<Method> methods = reflections.getMethodsAnnotatedWith(annotationType);
        methods.forEach(method -> acceptor.accept(method, method.getAnnotation(annotationType)));
    }

    public static final <A extends Annotation> void scanForAnnotatedFields( //
            @NonNull String packageName, //
            @NonNull Class<A> annotationType, //
            @NonNull BiConsumer<Field, A> acceptor, //
            ClassLoader... classLoaders) {

        Reflections reflections;
        synchronized (fieldScannerLock) {
            reflections = new Reflections(packageName, FIELD_ANNOTATIONS_SCANNER, classLoaders);
        }
        Set<Field> methods = reflections.getFieldsAnnotatedWith(annotationType);
        methods.forEach(method -> acceptor.accept(method, method.getAnnotation(annotationType)));
    }
}
