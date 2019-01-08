package io.gridgo.core.test;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.config.impl.JsonConfigurator;
import io.gridgo.core.support.config.impl.DefaultContextConfigurationParser;

public class ParserUnitTest {

    @Test
    public void testJson() {
        var json = JsonConfigurator.ofResource("test.json");
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
        Assert.assertEquals("class:io.gridgo.config.test.TestComponent", context.getComponentsContexts().get(0).getComponent());
        Assert.assertFalse(context.getGatewayContexts().get(0).getConnectorContexts().isEmpty());
        Assert.assertFalse(context.getGatewayContexts().get(0).getSubscriberContexts().isEmpty());
        Assert.assertEquals("http://localhost:8080", context.getGatewayContexts().get(0).getConnectorContexts().get(0).getEndpoint());
        Assert.assertEquals("class:io.gridgo.config.test.TestProcessor", context.getGatewayContexts().get(0).getSubscriberContexts().get(0).getProcessor());
        Assert.assertEquals("bean:executionStrategy", context.getGatewayContexts().get(0).getSubscriberContexts().get(1).getExecutionStrategy());
        Assert.assertEquals("payload.body.id > 0", context.getGatewayContexts().get(0).getSubscriberContexts().get(1).getCondition());
    }
}
