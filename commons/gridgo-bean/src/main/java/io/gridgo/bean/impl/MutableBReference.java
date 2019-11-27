package io.gridgo.bean.impl;

import io.gridgo.bean.BReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unchecked")
public class MutableBReference extends AbstractBElement implements BReference {

    @Setter
    @Getter
    private Object reference;

    @Override
    public boolean equals(Object other) {
        final Object myValue = this.getReference();
        Object otherValue = other;

        if (other instanceof BReference)
            otherValue = ((BReference) other).getReference();

        return myValue == null //
                ? otherValue == null //
                : myValue.equals(otherValue);

    }

    @Override
    public int hashCode() {
        return reference != null ? reference.hashCode() : super.hashCode();
    }
}