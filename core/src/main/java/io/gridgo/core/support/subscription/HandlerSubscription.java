package io.gridgo.core.support.subscription;

import org.joo.libra.Predicate;

import io.gridgo.framework.execution.ExecutionStrategy;
import io.gridgo.framework.execution.ExecutionStrategyInstrumenter;

public interface HandlerSubscription {

    public HandlerSubscription when(String condition);

    public HandlerSubscription when(Predicate condition);

    public HandlerSubscription using(ExecutionStrategy strategy);

    public HandlerSubscription instrumentWith(ExecutionStrategyInstrumenter instrumenter);

    public GatewaySubscription withPolicy(RoutingPolicy policy);

    public GatewaySubscription finishSubscribing();

    public RoutingPolicy getPolicy();
}
