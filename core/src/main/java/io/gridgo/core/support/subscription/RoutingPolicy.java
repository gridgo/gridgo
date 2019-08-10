package io.gridgo.core.support.subscription;

import java.util.Optional;
import java.util.function.Predicate;

import io.gridgo.core.Processor;
import io.gridgo.framework.execution.ExecutionStrategy;
import io.gridgo.framework.execution.ExecutionStrategyInstrumenter;
import io.gridgo.framework.support.Message;

public interface RoutingPolicy {

    public Optional<Predicate<Message>> getCondition();

    public Optional<ExecutionStrategy> getStrategy();

    public Optional<Predicate<Message>> getInstrumenterCondition();

    public Optional<ExecutionStrategyInstrumenter> getInstrumenter();

    public Processor getProcessor();

    public RoutingPolicy setCondition(Predicate<Message> condition);

    public RoutingPolicy setStrategy(ExecutionStrategy strategy);

    public RoutingPolicy setProcessor(Processor processor);

    public RoutingPolicy setInstrumenterCondition(Predicate<Message> condition);

    public RoutingPolicy setInstrumenter(ExecutionStrategyInstrumenter instrumenter);
}
