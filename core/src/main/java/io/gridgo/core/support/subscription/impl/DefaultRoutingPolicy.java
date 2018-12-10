package io.gridgo.core.support.subscription.impl;

import java.util.Optional;

import org.joo.libra.Predicate;

import io.gridgo.core.Processor;
import io.gridgo.core.support.subscription.RoutingPolicy;
import io.gridgo.framework.execution.ExecutionStrategy;
import lombok.Getter;

@Getter
public class DefaultRoutingPolicy implements RoutingPolicy {

    private Optional<Predicate> condition = Optional.empty();

    private Optional<ExecutionStrategy> strategy = Optional.empty();

    private Processor processor;

    public DefaultRoutingPolicy(Processor processor) {
        this.processor = processor;
    }

    @Override
    public RoutingPolicy setCondition(Predicate condition) {
        this.condition = Optional.ofNullable(condition);
        return this;
    }

    @Override
    public RoutingPolicy setStrategy(ExecutionStrategy strategy) {
        this.strategy = Optional.ofNullable(strategy);
        return this;
    }

    @Override
    public RoutingPolicy setProcessor(Processor processor) {
        this.processor = processor;
        return this;
    }
}
