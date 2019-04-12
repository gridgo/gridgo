package io.gridgo.bean;

import java.util.Collection;
import java.util.function.UnaryOperator;

public interface ImmutableBArray extends BArray {

    static final UnsupportedOperationException UNSUPPORTED = new UnsupportedOperationException("Instance of ImmutableBArray cannot be modified");

    @Override
    default void clear() {
        throw UNSUPPORTED;
    }

    @Override
    default boolean add(BElement e) {
        throw UNSUPPORTED;
    }

    @Override
    default boolean remove(Object o) {
        throw UNSUPPORTED;
    }

    @Override
    default boolean addAll(Collection<? extends BElement> c) {
        throw UNSUPPORTED;
    }

    @Override
    default boolean addAll(int index, Collection<? extends BElement> c) {
        throw UNSUPPORTED;
    }

    @Override
    default boolean removeAll(Collection<?> c) {
        throw UNSUPPORTED;
    }

    @Override
    default boolean retainAll(Collection<?> c) {
        throw UNSUPPORTED;
    }

    @Override
    default void replaceAll(UnaryOperator<BElement> operator) {
        throw UNSUPPORTED;
    }

    @Override
    default BElement set(int index, BElement element) {
        throw UNSUPPORTED;
    }

    @Override
    default void add(int index, BElement element) {
        throw UNSUPPORTED;
    }

    @Override
    default BElement remove(int index) {
        throw UNSUPPORTED;
    }

}
