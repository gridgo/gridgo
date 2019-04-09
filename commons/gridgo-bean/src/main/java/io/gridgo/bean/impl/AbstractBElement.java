package io.gridgo.bean.impl;

import io.gridgo.bean.BElement;
import io.gridgo.bean.serialization.BSerializerRegistry;
import lombok.Getter;
import lombok.Setter;

public abstract class AbstractBElement implements BElement {

    @Setter
    @Getter
    private BSerializerRegistry serializerRegistry;
}
