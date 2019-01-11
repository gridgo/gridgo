package io.gridgo.core;

import org.joo.libra.PredicateContext;

import io.gridgo.core.support.RoutingContext;

public interface RoutingPolicyEnforcer {

    public void execute(RoutingContext rc, GridgoContext gc, PredicateContext context);
}
