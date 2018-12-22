package io.gridgo.core.support.subscription.impl;

import org.joo.libra.Predicate;

import io.gridgo.core.Processor;
import io.gridgo.core.support.subscription.GatewaySubscription;
import io.gridgo.core.support.subscription.ProcessorSubscription;
import io.gridgo.core.support.subscription.RoutingPolicy;
import io.gridgo.framework.execution.ExecutionStrategy;
import io.gridgo.framework.execution.ExecutionStrategyInstrumenter;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class DefaultProcessorSubscription implements ProcessorSubscription {

    private GatewaySubscription gateway;

    private RoutingPolicy policy;

    public DefaultProcessorSubscription(GatewaySubscription gateway, Processor processor) {
        this.gateway = gateway;
        this.policy = new DefaultRoutingPolicy(processor);
    }

    public DefaultProcessorSubscription(GatewaySubscription gateway, RoutingPolicy policy) {
        this.gateway = gateway;
        this.policy = policy;
    }

    @Override
    public ProcessorSubscription instrumentWith(ExecutionStrategyInstrumenter instrumenter) {
        this.policy.setInstrumenter(instrumenter);
        return this;
    }

    @Override
    public ProcessorSubscription instrumentWhen(Predicate condition, ExecutionStrategyInstrumenter instrumenter) {
        this.policy.setInstrumenterCondition(condition);
        this.policy.setInstrumenter(instrumenter);
        return this;
    }

    @Override
    public ProcessorSubscription when(Predicate condition) {
        this.policy.setCondition(condition);
        return this;
    }

    @Override
    public ProcessorSubscription using(ExecutionStrategy strategy) {
        this.policy.setStrategy(strategy);
        return this;
    }

    @Override
    public GatewaySubscription withPolicy(final @NonNull RoutingPolicy policy) {
        this.policy.setCondition(policy.getCondition().orElse(null));
        this.policy.setStrategy(policy.getStrategy().orElse(null));
        return gateway;
    }

    @Override
    public GatewaySubscription finishSubscribing() {
        return gateway;
    }
}
