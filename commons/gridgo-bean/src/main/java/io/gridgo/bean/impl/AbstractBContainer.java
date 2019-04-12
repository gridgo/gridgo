package io.gridgo.bean.impl;

import io.gridgo.bean.BContainer;
import io.gridgo.bean.factory.BFactory;
import lombok.Getter;
import lombok.Setter;

public abstract class AbstractBContainer extends AbstractBElement implements BContainer {

    @Setter
    @Getter
    private BFactory factory;
}
