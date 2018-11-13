package io.gridgo.core.subscription;

import org.joo.libra.Predicate;

import io.gridgo.core.Gateway;
import io.gridgo.framework.execution.ExecutionStrategy;

public interface HandlerSubscription {

	public HandlerSubscription when(String condition);

	public HandlerSubscription when(Predicate condition);

	public HandlerSubscription using(ExecutionStrategy strategy);

	public Gateway withPolicy(RoutingPolicy policy);

	public Gateway finishSubscribing();
}
