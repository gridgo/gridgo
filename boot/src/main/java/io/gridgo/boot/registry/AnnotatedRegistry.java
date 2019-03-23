package io.gridgo.boot.registry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.gridgo.boot.support.annotations.AnnotationUtils;
import io.gridgo.boot.support.annotations.Registries;
import io.gridgo.boot.support.annotations.RegistryFactory;
import io.gridgo.boot.support.exceptions.InitializationException;
import io.gridgo.framework.support.Registry;

public class AnnotatedRegistry implements Registry {

    private Registry registry;

    public AnnotatedRegistry(Class<?> applicationClass) {
        this.registry = extractRegistryFromAnnotation(applicationClass);
    }

    private Registry extractRegistryFromAnnotation(Class<?> applicationClass) {
        var registries = new ArrayList<Registry>();
        String defaultProfile = null;
        var annotation = applicationClass.getAnnotation(Registries.class);
        if (annotation != null) {
            registries.addAll(getRegistriesFromClassAnnotation(annotation));
            defaultProfile = annotation.defaultProfile();
        }
        registries.addAll(getAllMethodsWithAnnotations(applicationClass));
        return new RegistryBuilder().setDefaultProfile(defaultProfile) //
                                    .setRegistries(registries.toArray(new Registry[0])) //
                                    .build();
    }

    private List<Registry> getAllMethodsWithAnnotations(Class<?> applicationClass) {
        var methods = AnnotationUtils.findAllMethodsWithAnnotation(applicationClass, RegistryFactory.class);
        return methods.stream() //
                      .map(this::instantiateRegistry) //
                      .collect(Collectors.toList());
    }

    private List<Registry> getRegistriesFromClassAnnotation(Registries annotation) {
        return Arrays.stream(annotation.registries()) //
                     .map(this::instantiateRegistry) //
                     .collect(Collectors.toList());
    }

    private Registry instantiateRegistry(Method method) {
        try {
            return (Registry) method.invoke(null);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new InitializationException("Cannot create registries", e);
        }
    }

    private Registry instantiateRegistry(Class<? extends Registry> registry) {
        try {
            return registry.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            throw new InitializationException("Cannot create registries", e);
        }
    }

    @Override
    public Object lookup(String name) {
        return registry.lookup(name);
    }

    @Override
    public Registry register(String name, Object answer) {
        registry.register(name, answer);
        return this;
    }
}
