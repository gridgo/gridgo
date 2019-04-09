package io.gridgo.config.event;

import io.gridgo.bean.BElement;

public interface ConfigurationChanged {

    public BElement getOldValue();
    
    public BElement getNewValue();
    
    public ChangedType getType();
}
