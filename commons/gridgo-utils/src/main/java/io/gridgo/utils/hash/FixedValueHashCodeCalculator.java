package io.gridgo.utils.hash;

public class FixedValueHashCodeCalculator<T> implements HashCodeCalculator<T> {

    private final int value;

    public FixedValueHashCodeCalculator(int value) {
        this.value = value;
    }

    @Override
    public int calcHashCode(T object) {
        return this.value;
    }

}
