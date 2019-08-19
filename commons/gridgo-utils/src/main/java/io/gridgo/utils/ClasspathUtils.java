package io.gridgo.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.function.BiConsumer;

import org.reflections.Reflections;

import lombok.NonNull;

public class ClasspathUtils {

    public static final <A extends Annotation> void scanForAnnotatedTypes( //
            @NonNull String packageName, //
            @NonNull Class<A> annotationType, //
            @NonNull BiConsumer<Class<?>, A> typeConsumer, //
            ClassLoader... classLoaders) {

        Reflections reflections = new Reflections(packageName, classLoaders);
        Set<Class<?>> types = reflections.getTypesAnnotatedWith(annotationType);
        types.forEach(clz -> typeConsumer.accept(clz, clz.getAnnotation(annotationType)));
    }

    public static final <A extends Annotation> void scanForAnnotatedMethod( //
            @NonNull String packageName, //
            @NonNull Class<A> annotationType, //
            @NonNull BiConsumer<Method, A> acceptor, //
            ClassLoader... classLoaders) {

        Reflections reflections = new Reflections(packageName, classLoaders);
        Set<Method> methods = reflections.getMethodsAnnotatedWith(annotationType);
        methods.forEach(method -> acceptor.accept(method, method.getAnnotation(annotationType)));
    }
}
