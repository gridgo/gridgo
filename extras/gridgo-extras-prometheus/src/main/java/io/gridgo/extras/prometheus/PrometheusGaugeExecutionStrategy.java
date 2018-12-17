package io.gridgo.extras.prometheus;

import io.gridgo.framework.execution.ExecutionStrategy;
import io.gridgo.framework.execution.impl.WrappedExecutionStrategy;
import io.prometheus.client.Gauge;
import lombok.Getter;

public class PrometheusGaugeExecutionStrategy extends WrappedExecutionStrategy {

    @Getter
    private Gauge gauge;

    public PrometheusGaugeExecutionStrategy(ExecutionStrategy strategy, String name, String help) {
        super(strategy);
        this.gauge = Gauge.build(name, help).register();
    }

    public PrometheusGaugeExecutionStrategy(ExecutionStrategy strategy, Gauge gauge) {
        super(strategy);
        this.gauge = gauge;
    }

    @Override
    protected void wrap(Runnable runnable) {
        gauge.inc();
        runnable.run();
        gauge.dec();
    }
}
