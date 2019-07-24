package io.gridgo.bean.impl;

import java.util.Map;

import io.gridgo.bean.BElement;
import lombok.NonNull;
import lombok.experimental.Delegate;

@SuppressWarnings("unchecked")
public class MutableBObject extends AbstractBObject {

    @Delegate
    private final Map<String, BElement> holder;

    public MutableBObject(@NonNull Map<String, BElement> holder) {
        this.holder = holder;
    }
}
