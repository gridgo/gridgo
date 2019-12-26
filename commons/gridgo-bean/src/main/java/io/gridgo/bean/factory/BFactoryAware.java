package io.gridgo.bean.factory;

import io.gridgo.utils.annotations.Transient;

public interface BFactoryAware {

    @Transient
    void setFactory(BFactory factory);

    @Transient
    default BFactory getFactory() {
        return BFactory.DEFAULT;
    }
}
