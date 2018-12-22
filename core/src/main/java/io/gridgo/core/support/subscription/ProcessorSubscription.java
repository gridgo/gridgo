package io.gridgo.core.support.subscription;

import java.util.function.Supplier;

import org.joo.libra.Predicate;

import io.gridgo.core.support.subscription.impl.Condition;
import io.gridgo.framework.execution.ExecutionStrategy;
import io.gridgo.framework.execution.ExecutionStrategyInstrumenter;
import io.gridgo.framework.support.Message;

public interface ProcessorSubscription {

    public default ProcessorSubscription when(String condition) {
        return when(Condition.of(condition));
    }

    public default ProcessorSubscription when(java.util.function.Predicate<Message> condition) {
        return when(Condition.of(condition));
    }

    public ProcessorSubscription when(Predicate condition);

    public ProcessorSubscription using(ExecutionStrategy strategy);

    public ProcessorSubscription instrumentWith(ExecutionStrategyInstrumenter instrumenter);

    public default ProcessorSubscription instrumentWhen(Supplier<Boolean> condition,
            ExecutionStrategyInstrumenter instrumenter) {
        if (condition.get())
            instrumentWith(instrumenter);
        return this;
    }

    public default ProcessorSubscription instrumentWhen(String condition, ExecutionStrategyInstrumenter instrumenter) {
        return instrumentWhen(Condition.of(condition), instrumenter);
    }

    public default ProcessorSubscription instrumentWhen(java.util.function.Predicate<Message> condition,
            ExecutionStrategyInstrumenter instrumenter) {
        return instrumentWhen(Condition.of(condition), instrumenter);
    }

    public ProcessorSubscription instrumentWhen(Predicate condition, ExecutionStrategyInstrumenter instrumenter);

    public GatewaySubscription withPolicy(RoutingPolicy policy);

    public GatewaySubscription finishSubscribing();

    public RoutingPolicy getPolicy();
}
