package io.gridgo.bean.impl;

import io.gridgo.bean.BReference;
import io.gridgo.utils.pojo.getter.PojoGetterProxy;
import io.gridgo.utils.pojo.setter.PojoSetterProxy;
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

    private PojoSetterProxy setterProxy;

    private PojoGetterProxy getterProxy;

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

    @Override
    public PojoGetterProxy getterProxy() {
        if (this.getterProxy == null)
            return BReference.super.getterProxy();
        return this.getterProxy;
    }

    @Override
    public void getterProxy(PojoGetterProxy getterProxy) {
        this.getterProxy = getterProxy;
    }

    @Override
    public PojoSetterProxy setterProxy() {
        if (this.setterProxy == null)
            return BReference.super.setterProxy();
        return this.setterProxy;
    }

    @Override
    public void setterProxy(PojoSetterProxy setterProxy) {
        this.setterProxy = setterProxy;
    }

}