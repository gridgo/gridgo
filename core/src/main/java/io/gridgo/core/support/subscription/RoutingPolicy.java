package io.gridgo.core.support.subscription;

import java.util.Optional;

import org.joo.libra.Predicate;

import io.gridgo.core.Processor;
import io.gridgo.framework.execution.ExecutionStrategy;

public interface RoutingPolicy {

	public Optional<Predicate> getCondition();

	public Optional<ExecutionStrategy> getStrategy();

	public Processor getProcessor();

	public RoutingPolicy setCondition(Predicate condition);

	public RoutingPolicy setStrategy(ExecutionStrategy strategy);

	public RoutingPolicy setProcessor(Processor processor);
}
