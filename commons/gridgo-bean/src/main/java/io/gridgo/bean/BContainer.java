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

    @Override
    default boolean isValue() {
        return false;
    }

    @Override
    default boolean isObject() {
        return false;
    }

    @Override
    default boolean isArray() {
        return false;
    }

    @Override
    default boolean isReference() {
        return false;
    }
}
