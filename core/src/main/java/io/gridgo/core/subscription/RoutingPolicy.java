package io.gridgo.core.subscription;

import java.util.function.BiConsumer;

import org.joo.libra.Predicate;

import io.gridgo.core.GridgoContext;
import io.gridgo.core.context.RoutingContext;
import io.gridgo.framework.execution.ExecutionStrategy;

public interface RoutingPolicy {

	public Predicate getCondition();

	public ExecutionStrategy getStrategy();

	public BiConsumer<RoutingContext, GridgoContext> getHandler();
}
