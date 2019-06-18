package io.gridgo.bean.serialization;

import io.gridgo.bean.factory.BFactory;
import io.gridgo.bean.factory.BFactoryAware;
import lombok.Getter;
import lombok.Setter;

public abstract class AbstractBSerializer implements BSerializer, BFactoryAware {

    @Setter
    @Getter
    private BFactory factory;
}
