package io.gridgo.extras.yaml.test;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.extras.yaml.YamlConfigurator;

public class YamlUnitTest {

    @Test
    public void testYaml() {
        var yaml = YamlConfigurator.ofResource("test.yml");
        yaml.start();
        var config = yaml.get().orElseThrow().asObject();
        Assert.assertEquals("test", config.getString("applicationName"));
        Assert.assertNotNull(config.getObject("gateways"));
        Assert.assertNotNull(config.getObject("gateways").getObject("test"));
        Assert.assertFalse(config.getObject("gateways").getObject("test").getArray("subscribers").isEmpty());
    }
}
