package io.gridgo.utils.helper;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.function.BiConsumer;

import org.reflections.Reflections;

import lombok.NonNull;

public interface ClasspathScanner {

    void scan(String packageName, ClassLoader... classLoaders);

    default <A extends Annotation> void scanForAnnotatedTypes( //
            @NonNull String packageName, //
            @NonNull Class<A> annotationType, //
            @NonNull BiConsumer<Class<?>, A> typeConsumer, //
            ClassLoader... classLoaders) {

        Reflections reflections = new Reflections(packageName, classLoaders);
        Set<Class<?>> annotatedTypes = reflections.getTypesAnnotatedWith(annotationType);
        for (Class<?> clz : annotatedTypes) {
            A annotation = clz.getAnnotation(annotationType);
            typeConsumer.accept(clz, annotation);
        }
    }
}
