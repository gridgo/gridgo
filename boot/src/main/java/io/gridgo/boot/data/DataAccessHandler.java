package io.gridgo.boot.data;

import java.lang.reflect.InvocationHandler;

import io.gridgo.core.Gateway;
import io.gridgo.core.GridgoContext;

public interface DataAccessHandler extends InvocationHandler {

    public void setContext(GridgoContext context);
    
    public void setGateway(Gateway gateway);
}
