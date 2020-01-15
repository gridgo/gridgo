package io.gridgo.bean.impl;

import io.gridgo.bean.BElement;
import io.gridgo.bean.serialization.BSerializerRegistry;
import io.gridgo.utils.annotations.Transient;
import lombok.Getter;
import lombok.Setter;

public abstract class AbstractBElement implements BElement {

    @Setter
    @Getter
    @Transient
    private transient BSerializerRegistry serializerRegistry;

    @Override
    public String toString() {
        return new String(this.toBytes("print"));
    }
}
