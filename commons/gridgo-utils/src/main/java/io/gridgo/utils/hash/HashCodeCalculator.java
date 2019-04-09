package io.gridgo.utils.hash;

public interface HashCodeCalculator<T> {

    int calcHashCode(T object);
}
