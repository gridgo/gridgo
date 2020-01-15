package io.gridgo.bean;

import io.gridgo.bean.factory.BFactoryAware;

public interface BContainer extends BElement, BFactoryAware {

    int size();

    boolean isEmpty();

    void clear();

    @Override
    default boolean isContainer() {
        return true;
    }
}
