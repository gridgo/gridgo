package io.gridgo.core.support.subscription;

import java.util.function.BooleanSupplier;

import org.joo.libra.Predicate;

import io.gridgo.core.support.subscription.impl.Condition;
import io.gridgo.framework.execution.ExecutionStrategy;
import io.gridgo.framework.execution.ExecutionStrategyInstrumenter;
import io.gridgo.framework.support.Message;

/**
 * Represents a processor subscription. This class is used for a "fluent" API,
 * so that you can modify the condition, execution strategy of the processor
 * after subscribing to a gateay.
 */
public interface ProcessorSubscription {

    /**
     * Set the condition of the processor to run, using
     * <code>org.joo.libra.sql.SqlPredicate</code> syntax.
     * 
     * @param condition the condition
     * @return the current object
     */
    public default ProcessorSubscription when(String condition) {
        return when(Condition.of(condition));
    }

    /**
     * Set the condition of the processor to run, using Java Predicate.
     * 
     * @param condition the condition
     * @return the current object
     */
    public default ProcessorSubscription when(java.util.function.Predicate<Message> condition) {
        return when(Condition.of(condition));
    }

    /**
     * Set the condition of the processor to run, using a custom
     * <code>org.joo.libra.Predicate</code>
     * 
     * @param condition the condition
     * @return the current object
     */
    public ProcessorSubscription when(Predicate condition);

    /**
     * Set the execution strategy of the processor to run.
     * 
     * @param strategy the strategy
     * @return the current object
     */
    public ProcessorSubscription using(ExecutionStrategy strategy);

    /**
     * Instrument the processor with an <code>ExecutionStrategyInstrumenter</code>
     * 
     * @param instrumenter the instrumenter
     * @return the current object
     */
    public ProcessorSubscription instrumentWith(ExecutionStrategyInstrumenter instrumenter);

    /**
     * Instrument the processor with an <code>ExecutionStrategyInstrumenter</code>,
     * if the condition resolved to true.
     * 
     * @param condition    the condition
     * @param instrumenter the instrumenter
     * @return the current object
     */
    public default ProcessorSubscription instrumentWhen(BooleanSupplier condition,
            ExecutionStrategyInstrumenter instrumenter) {
        if (condition.getAsBoolean())
            instrumentWith(instrumenter);
        return this;
    }

    /**
     * Instrument the processor with an <code>ExecutionStrategyInstrumenter</code>,
     * if the condition resolved to true.
     * 
     * @param condition    the condition
     * @param instrumenter the instrumenter
     * @return the current object
     */
    public default ProcessorSubscription instrumentWhen(String condition, ExecutionStrategyInstrumenter instrumenter) {
        return instrumentWhen(Condition.of(condition), instrumenter);
    }

    /**
     * Instrument the processor with an <code>ExecutionStrategyInstrumenter</code>,
     * if the condition resolved to true.
     * 
     * @param condition    the condition
     * @param instrumenter the instrumenter
     * @return the current object
     */
    public default ProcessorSubscription instrumentWhen(java.util.function.Predicate<Message> condition,
            ExecutionStrategyInstrumenter instrumenter) {
        return instrumentWhen(Condition.of(condition), instrumenter);
    }

    /**
     * Instrument the processor with an <code>ExecutionStrategyInstrumenter</code>,
     * if the condition resolved to true.
     * 
     * @param condition    the condition
     * @param instrumenter the instrumenter
     * @return the current object
     */
    public ProcessorSubscription instrumentWhen(Predicate condition, ExecutionStrategyInstrumenter instrumenter);

    /**
     * Copy another routing policy configuration.
     * 
     * @param policy the routing policy
     * @return the GatewaySubscription which this processor is subscribed
     */
    public GatewaySubscription withPolicy(RoutingPolicy policy);

    /**
     * Finish subscribing this processor.
     * 
     * @return the GatewaySubscription which this processor is subscribed
     */
    public GatewaySubscription finishSubscribing();

    /**
     * Get the routing policy of this processor
     * 
     * @return the routing policy
     */
    public RoutingPolicy getPolicy();
}
