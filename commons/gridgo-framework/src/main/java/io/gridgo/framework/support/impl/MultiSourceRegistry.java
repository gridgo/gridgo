package io.gridgo.framework.support.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import io.gridgo.framework.support.Registry;
import lombok.NonNull;

public class MultiSourceRegistry extends SimpleRegistry {

    private Registry[] registries;

    public MultiSourceRegistry(Registry... registries) {
        this.registries = registries;
    }

    public MultiSourceRegistry(@NonNull Collection<Registry> registries) {
        this.registries = registries.toArray(new Registry[0]);
    }

    @Override
    public Object lookup(String name) {
        var answer = super.lookup(name);
        if (answer != null)
            return answer;
        return Arrays.stream(registries) //
                     .map(registry -> registry.lookup(name)) //
                     .filter(Objects::nonNull) //
                     .findAny().orElse(null);
    }

    @Override
    public Object lookupByType(Class<?> type) {
        return Arrays.stream(registries) //
                     .map(registry -> registry.lookupByType(type)) //
                     .filter(Objects::nonNull) //
                     .findAny().orElse(null);
    }
}
