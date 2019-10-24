package io.gridgo.bean.impl;

import java.util.List;

import io.gridgo.bean.BElement;
import lombok.NonNull;
import lombok.experimental.Delegate;

@SuppressWarnings("unchecked")
public class MutableBArray extends AbstractBArray {

    @Delegate
    private final List<BElement> holder;

    public MutableBArray(@NonNull List<BElement> holder) {
        this.holder = holder;
    }
}