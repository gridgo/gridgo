package io.gridgo.extras.prometheus;

import io.gridgo.framework.execution.ExecutionStrategyInstrumenter;
import io.prometheus.client.Gauge;
import lombok.Getter;

public class PrometheusGaugeInstrumenter implements ExecutionStrategyInstrumenter {

    @Getter
    private Gauge gauge;

    public PrometheusGaugeInstrumenter(String name, String help) {
        this.gauge = Gauge.build(name, help).register();
    }

    public PrometheusGaugeInstrumenter(Gauge gauge) {
        this.gauge = gauge;
    }

    @Override
    public Runnable instrument(Runnable runnable) {
        return () -> {
            gauge.inc();
            runnable.run();
            gauge.dec();
        };
    }
}
