package io.gridgo.extras.prometheus.test;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.core.GridgoContext;
import io.gridgo.core.impl.DefaultGridgoContextBuilder;
import io.gridgo.core.support.RoutingContext;
import io.gridgo.extras.prometheus.PrometheusCounterInstrumenter;
import io.gridgo.extras.prometheus.PrometheusGaugeInstrumenter;
import io.gridgo.extras.prometheus.PrometheusHistorgramTimeInstrumenter;
import io.gridgo.extras.prometheus.PrometheusSummaryTimeInstrumenter;
import io.gridgo.framework.execution.ExecutionStrategyInstrumenter;
import io.gridgo.framework.support.Message;

public class PrometheusInstrumenterUnitTest {

    @Test
    public void testCounter() {
        var counter = new PrometheusCounterInstrumenter("counter", "test");
        var context = buildContext(counter);
        context.start();
        context.findGatewayMandatory("test").push(Message.ofEmpty().attachSource("test"));
        context.findGatewayMandatory("test").push(Message.ofEmpty().attachSource("test"));
        Assert.assertEquals(2, (int) counter.getCounter().get());
        context.stop();
    }

    @Test
    public void testGauge() {
        var counter = new PrometheusGaugeInstrumenter("gauge", "test");
        var context = buildContext(counter);
        context.start();
        context.findGatewayMandatory("test").push(Message.ofEmpty().attachSource("test"));
        context.findGatewayMandatory("test").push(Message.ofEmpty().attachSource("test"));
        Assert.assertEquals(0, (int) counter.getGauge().get());
        context.stop();
    }

    @Test
    public void testHistogram() {
        var counter = new PrometheusHistorgramTimeInstrumenter("histogram", "test");
        var context = buildContext(counter);
        context.start();
        context.findGatewayMandatory("test").push(Message.ofEmpty().attachSource("test"));
        context.findGatewayMandatory("test").push(Message.ofEmpty().attachSource("test"));
        Assert.assertFalse(counter.getHistogram().collect().isEmpty());
        context.stop();
    }

    @Test
    public void testSummary() {
        var counter = new PrometheusSummaryTimeInstrumenter("summary", "test");
        var context = buildContext(counter);
        context.start();
        context.findGatewayMandatory("test").push(Message.ofEmpty().attachSource("test"));
        context.findGatewayMandatory("test").push(Message.ofEmpty().attachSource("test"));
        Assert.assertEquals(2, (int) counter.getSummary().get().count);
        context.stop();
    }

    private GridgoContext buildContext(ExecutionStrategyInstrumenter instrumenter) {
        var context = new DefaultGridgoContextBuilder().build();
        context.openGateway("test") //
               .subscribe(this::discard) //
               .instrumentWith(instrumenter);
        return context;
    }

    private void discard(RoutingContext rc, GridgoContext gc) {
        // Nothing to do here
    }
}
