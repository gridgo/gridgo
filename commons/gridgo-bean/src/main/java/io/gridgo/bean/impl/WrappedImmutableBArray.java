package io.gridgo.bean.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.gridgo.bean.ImmutableBArray;
import io.gridgo.bean.WrappedBArray;
import lombok.Getter;
import lombok.NonNull;

@SuppressWarnings("unchecked")
public class WrappedImmutableBArray extends AbstractBArray implements WrappedBArray, ImmutableBArray {

    @Getter
    private final List<?> source;

    public WrappedImmutableBArray(@NonNull Collection<?> collection) {
        source = collection instanceof List ? ((List<?>) collection) : new ArrayList<>(collection);
    }
}
