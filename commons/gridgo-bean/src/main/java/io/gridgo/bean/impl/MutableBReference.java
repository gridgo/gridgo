package io.gridgo.bean.impl;

import io.gridgo.bean.BReference;
import io.gridgo.bean.serialization.text.BPrinter;
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
    public String toString() {
        StringBuilder writer = new StringBuilder();
        BPrinter.print(writer, this);
        return writer.toString();
    }

    @Override
    public boolean equals(Object obj) {
        final Object myValue = this.getReference();
        final Object other;
        if (obj instanceof BReference) {
            other = ((BReference) obj).getReference();
        } else {
            other = obj;
        }
        return myValue == null //
                ? other == null //
                : myValue.equals(obj);
    }

    @Override
    public int hashCode() {
        return reference != null ? reference.hashCode() : super.hashCode();
    }
}