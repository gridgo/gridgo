package io.gridgo.framework.support.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import io.gridgo.framework.support.Registry;
import lombok.NonNull;

public class MultiSourceRegistry extends SimpleRegistry {

    private List<Registry> registries = new CopyOnWriteArrayList<>();

    public MultiSourceRegistry(Registry... registries) {
        this.registries.addAll(Arrays.asList(registries));
    }

    public MultiSourceRegistry(@NonNull Collection<Registry> registries) {
        this.registries.addAll(registries);
    }

    @Override
    public Object lookup(String name) {
        var answer = super.lookup(name);
        if (answer != null)
            return answer;
        return registries.stream() //
                         .map(registry -> registry.lookup(name)) //
                         .filter(Objects::nonNull) //
                         .findAny().orElse(null);
    }

    @Override
    public Object lookupByType(Class<?> type) {
        return registries.stream() //
                         .map(registry -> registry.lookupByType(type)) //
                         .filter(Objects::nonNull) //
                         .findAny().orElse(null);
    }

    public MultiSourceRegistry addRegistry(Registry registry) {
        registries.add(registry);
        return this;
    }
}
