package io.gridgo.core.support.subscription;

import org.joo.libra.Predicate;

import io.gridgo.core.Processor;
import io.gridgo.framework.execution.ExecutionStrategy;

public interface RoutingPolicy {

	public Predicate getCondition();

	public ExecutionStrategy getStrategy();

	public Processor getProcessor();

	public RoutingPolicy setCondition(Predicate condition);

	public RoutingPolicy setStrategy(ExecutionStrategy strategy);

	public RoutingPolicy setProcessor(Processor processor);
}
