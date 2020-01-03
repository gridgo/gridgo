package io.gridgo.config.test;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.config.impl.JsonConfigurator;
import io.gridgo.connector.support.config.impl.DefaultConnectorContextBuilder;
import io.gridgo.core.impl.ConfiguratorContextBuilder;
import io.gridgo.core.support.config.impl.DefaultContextConfigurationParser;
import io.gridgo.framework.execution.ExecutionStrategyInstrumenter;
import io.gridgo.framework.execution.impl.ExecutorExecutionStrategy;
import io.gridgo.framework.support.impl.SimpleRegistry;

public class ParserUnitTest {

    @Test
    public void testParser() {
        var json = JsonConfigurator.ofResource("test99.json");
        json.start();
        var parser = new DefaultContextConfigurationParser();
        var obj = json.get().orElseThrow();
        var context = parser.parse(obj.asObject());
        Assert.assertNotNull(context);
        Assert.assertEquals("test", context.getApplicationName());
        Assert.assertNotNull(context.getComponentsContexts());
        Assert.assertNotNull(context.getGatewayContexts());
        Assert.assertFalse(context.getComponentsContexts().isEmpty());
        Assert.assertFalse(context.getGatewayContexts().isEmpty());
        Assert.assertEquals("class:io.gridgo.config.test.TestComponent",
                context.getComponentsContexts().get(0).getComponent());
        Assert.assertFalse(context.getGatewayContexts().get(0).getConnectorContexts().isEmpty());
        Assert.assertFalse(context.getGatewayContexts().get(0).getSubscriberContexts().isEmpty());
        Assert.assertEquals("test99:xyz",
                context.getGatewayContexts().get(0).getConnectorContexts().get(0).getEndpoint());
        Assert.assertEquals("class:io.gridgo.config.test.TestProcessor",
                context.getGatewayContexts().get(0).getSubscriberContexts().get(0).getProcessor());
        Assert.assertEquals("bean:executionStrategy",
                context.getGatewayContexts().get(0).getSubscriberContexts().get(1).getExecutionStrategy());
        Assert.assertEquals("payload.body.id > 0",
                context.getGatewayContexts().get(0).getSubscriberContexts().get(1).getCondition());
    }

    @Test
    public void testContextBuilder() {
        var registry = new SimpleRegistry().register("xyz", 1) //
                                           .register("abc", 2) //
                                           .register("executionStrategy", new ExecutorExecutionStrategy(1))
                                           .register("testInstrumenter",
                                                   (ExecutionStrategyInstrumenter) (msg, deferred, r) -> r)
                                           .register("testInstrumenter2",
                                                   (ExecutionStrategyInstrumenter) (msg, deferred, r) -> r);
        registry.register("connectorContextBuilder", new DefaultConnectorContextBuilder().setRegistry(registry));
        var configurator = JsonConfigurator.ofResource("test99.json");
        var context = new ConfiguratorContextBuilder().setConfigurator(configurator).setRegistry(registry).build();
        Assert.assertTrue(context.findGateway("test").isPresent());
    }
}
