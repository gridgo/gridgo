package io.gridgo.config.test;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.config.impl.JsonConfigurator;

public class ConfiguratorUnitTest {

    @Test
    public void testJson() {
        var json = JsonConfigurator.ofResource("test.json");
        json.start();
        var obj = json.get().orElseThrow();
        Assert.assertNotNull(obj);
        Assert.assertEquals("test", obj.asObject().getString("applicationName"));
    }
}
