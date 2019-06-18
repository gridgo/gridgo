package io.gridgo.bean.factory;

public interface BFactoryAware {

    void setFactory(BFactory factory);

    default BFactory getFactory() {
        return BFactory.DEFAULT;
    }
}
