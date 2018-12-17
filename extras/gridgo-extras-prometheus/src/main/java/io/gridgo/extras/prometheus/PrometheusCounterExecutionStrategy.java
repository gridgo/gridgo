package io.gridgo.extras.prometheus;

import io.gridgo.framework.execution.ExecutionStrategy;
import io.gridgo.framework.execution.impl.WrappedExecutionStrategy;
import io.prometheus.client.Counter;
import lombok.Getter;

public class PrometheusCounterExecutionStrategy extends WrappedExecutionStrategy {

    @Getter
    private Counter counter;

    public PrometheusCounterExecutionStrategy(ExecutionStrategy strategy, String name, String help) {
        super(strategy);
        this.counter = Counter.build().name(name).help(help).register();
    }

    public PrometheusCounterExecutionStrategy(ExecutionStrategy strategy, Counter counter) {
        super(strategy);
        this.counter = counter;
    }

    @Override
    protected void wrap(Runnable runnable) {
        counter.inc();
        runnable.run();
    }
}
