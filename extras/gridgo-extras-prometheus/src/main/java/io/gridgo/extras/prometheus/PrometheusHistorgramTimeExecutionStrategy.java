package io.gridgo.extras.prometheus;

import io.gridgo.framework.execution.ExecutionStrategy;
import io.gridgo.framework.execution.impl.WrappedExecutionStrategy;
import io.prometheus.client.Histogram;
import lombok.Getter;

public class PrometheusHistorgramTimeExecutionStrategy extends WrappedExecutionStrategy {

    @Getter
    private Histogram histogram;

    public PrometheusHistorgramTimeExecutionStrategy(ExecutionStrategy strategy, String name, String help) {
        super(strategy);
        this.histogram = Histogram.build(name, help).register();
    }

    public PrometheusHistorgramTimeExecutionStrategy(ExecutionStrategy strategy, String name, String help,
            double... buckets) {
        super(strategy);
        this.histogram = Histogram.build(name, help).buckets(buckets).register();
    }

    public PrometheusHistorgramTimeExecutionStrategy(ExecutionStrategy strategy, Histogram histogram) {
        super(strategy);
        this.histogram = histogram;
    }

    @Override
    protected void wrap(Runnable runnable) {
        histogram.time(runnable);
    }
}
