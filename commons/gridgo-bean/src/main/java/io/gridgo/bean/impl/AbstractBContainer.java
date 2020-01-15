package io.gridgo.bean.impl;

import io.gridgo.bean.BContainer;
import io.gridgo.bean.factory.BFactory;
import io.gridgo.utils.annotations.Transient;
import lombok.Getter;
import lombok.Setter;

public abstract class AbstractBContainer extends AbstractBElement implements BContainer {

    @Setter
    @Getter
    @Transient
    private transient BFactory factory;
}
