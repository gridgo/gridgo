package io.gridgo.extras.prometheus;

import io.gridgo.framework.execution.ExecutionStrategyInstrumenter;
import io.prometheus.client.Histogram;
import lombok.Getter;

public class PrometheusHistorgramTimeInstrumenter implements ExecutionStrategyInstrumenter {

    @Getter
    private Histogram histogram;

    public PrometheusHistorgramTimeInstrumenter(String name, String help) {
        this.histogram = Histogram.build(name, help).register();
    }

    public PrometheusHistorgramTimeInstrumenter(String name, String help, double... buckets) {
        this.histogram = Histogram.build(name, help).buckets(buckets).register();
    }

    public PrometheusHistorgramTimeInstrumenter(Histogram histogram) {
        this.histogram = histogram;
    }

    @Override
    public Runnable instrument(Runnable runnable) {
        return () -> histogram.time(runnable);
    }
}
