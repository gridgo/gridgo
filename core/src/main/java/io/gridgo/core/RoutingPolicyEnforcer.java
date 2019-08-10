package io.gridgo.core;

import io.gridgo.core.support.RoutingContext;
import io.gridgo.framework.support.Message;

public interface RoutingPolicyEnforcer {

    public void execute(RoutingContext rc, GridgoContext gc, Message context);
}
