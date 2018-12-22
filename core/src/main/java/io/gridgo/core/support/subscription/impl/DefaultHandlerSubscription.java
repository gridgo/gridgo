package io.gridgo.core.support.subscription.impl;

import org.joo.libra.Predicate;
import org.joo.libra.sql.SqlPredicate;

import io.gridgo.core.Processor;
import io.gridgo.core.support.subscription.GatewaySubscription;
import io.gridgo.core.support.subscription.HandlerSubscription;
import io.gridgo.core.support.subscription.RoutingPolicy;
import io.gridgo.framework.execution.ExecutionStrategy;
import io.gridgo.framework.execution.ExecutionStrategyInstrumenter;
import io.gridgo.framework.support.Message;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class DefaultHandlerSubscription implements HandlerSubscription {

    private GatewaySubscription gateway;

    private RoutingPolicy policy;

    public DefaultHandlerSubscription(GatewaySubscription gateway, Processor processor) {
        this.gateway = gateway;
        this.policy = new DefaultRoutingPolicy(processor);
    }

    public DefaultHandlerSubscription(GatewaySubscription gateway, RoutingPolicy policy) {
        this.gateway = gateway;
        this.policy = policy;
    }

    @Override
    public HandlerSubscription instrumentWith(ExecutionStrategyInstrumenter instrumenter) {
        this.policy.setInstrumenter(instrumenter);
        return this;
    }

    @Override
    public HandlerSubscription when(String condition) {
        return when(new SqlPredicate(condition));
    }

    @Override
    public HandlerSubscription when(Predicate condition) {
        this.policy.setCondition(condition);
        return this;
    }

    @Override
    public HandlerSubscription when(java.util.function.Predicate<Message> condition) {
        this.policy.setCondition(new MessagePredicate(condition));
        return this;
    }

    @Override
    public HandlerSubscription using(ExecutionStrategy strategy) {
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
