package io.gridgo.core.support.config;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import io.gridgo.core.support.exceptions.AmbiguousException;
import io.gridgo.framework.support.Registry;
import io.gridgo.framework.support.exceptions.BeanNotFoundException;

public interface ConfiguratorResolver {

    public Registry getRegistry();

    @SuppressWarnings("unchecked")
    public default <T> T resolve(String bean) {
        if (bean == null)
            return null;
        var registry = getRegistry();
        var frags = bean.split(":");
        if ("bean".equals(frags[0]))
            return (T) resolveBean(frags[1], registry);
        if ("class".equals(frags[0]))
            return (T) resolveClass(frags[1], registry);
        return (T) resolveRaw(frags[1], registry);
    }

    public default String resolveRaw(String name, Registry registry) {
        return registry.substituteRegistriesRecursive(name);
    }

    public default Object resolveClass(String name, Registry registry) {
        try {
            var clazz = Class.forName(name);
            var constructors = clazz.getConstructors();
            if (constructors.length > 1)
                throw new AmbiguousException("Only one constructor is allowed");
            var constructor = constructors[0];
            var params = Arrays.stream(constructor.getParameterTypes()) //
                    .map(type -> lookupForType(registry, type)) //
                    .toArray(size -> new Object[size]);
            return constructor.newInstance(params);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public default Object lookupForType(Registry registry, Class<?> type) {
        if (type == Registry.class)
            return registry;
        throw new BeanNotFoundException("Cannot find any bean with the required type " + type.getName());
    }

    public default Object resolveBean(String name, Registry registry) {
        return registry.lookupMandatory(name);
    }
}
