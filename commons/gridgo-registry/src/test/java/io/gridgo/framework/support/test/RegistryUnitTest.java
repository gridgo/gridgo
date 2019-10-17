package io.gridgo.framework.support.test;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;

import io.gridgo.framework.support.exceptions.BeanNotFoundException;
import io.gridgo.framework.support.impl.MultiSourceRegistry;
import io.gridgo.framework.support.impl.PropertiesFileRegistry;
import io.gridgo.framework.support.impl.SimpleRegistry;
import io.gridgo.framework.support.impl.XmlQueryRegistry;
import io.gridgo.framework.support.impl.XmlRegistry;

public class RegistryUnitTest {

    @Test
    public void testPrimitive() {
        var registry = new SimpleRegistry().register("flag", "true");
        Assert.assertTrue(registry.lookup("flag", Boolean.class));
    }

    @Test
    public void testXml() {
        var registry = XmlRegistry.ofResource("test-registry.xml");
        Assert.assertEquals("value1", registry.lookup("/root/item[@name='key1']"));
        Assert.assertEquals("value2", registry.lookup("/root/item[@name='key2']"));

        var queryRegistry = new XmlQueryRegistry(XmlRegistry.ofResource("test-query.xml"));
        Assert.assertEquals("select * from t1", queryRegistry.lookup("key1"));
        Assert.assertEquals("select * from t2", queryRegistry.lookup("key2"));
    }

    @Test
    public void testPropertyRegistry() {
        var classLoader = getClass().getClassLoader();
        var file = new File(classLoader.getResource("test.properties").getFile());
        var registry = new PropertiesFileRegistry(file);
        Assert.assertEquals("hello", registry.lookup("msg"));
        registry = new PropertiesFileRegistry(file.getAbsolutePath());
        Assert.assertEquals("hello", registry.lookup("msg"));
        registry.register("msg", "world");
        Assert.assertEquals("world", registry.lookup("msg"));
    }

    @Test
    public void testRegistry() throws InterruptedException {
        var reg = new SimpleRegistry().register("name", "dungba").register("age", 10);
        Assert.assertEquals("dungba", reg.lookup("name"));
        Assert.assertEquals(10, reg.lookup("age"));
        Assert.assertNull(reg.lookup("dob"));
        try {
            reg.lookupMandatory("dob");
            Assert.fail("must fail");
        } catch (BeanNotFoundException ex) {

        }
        try {
            reg.lookupMandatory("age", List.class);
            Assert.fail("must fail");
        } catch (ClassCastException ex) {

        }
        int i = reg.lookupMandatory("age", Integer.class);
        Assert.assertEquals(10, i);
    }

    @Test
    public void testMultiSource() {
        var registry1 = new SimpleRegistry().register("key1", "value1");
        var registry2 = new SimpleRegistry().register("key1", "value2")
                .register("key2", "value2");
        var reg = new MultiSourceRegistry(registry1, registry2);
        Assert.assertEquals("value1", reg.lookup("key1", String.class));
        Assert.assertEquals("value2", reg.lookup("key2", String.class));
        reg.register("key1", "value3");
        Assert.assertEquals("value3", reg.lookup("key1", String.class));
        reg.addRegistry(new SimpleRegistry().register("key4", "value4"));
        Assert.assertEquals("value4", reg.lookup("key4", String.class));
    }

    @Test
    public void testSubstitute() {
        var reg = new SimpleRegistry().register("key1", "value1=${key2}")
                .register("key2", "value2");
        Assert.assertEquals("value1=${key2}", reg.lookup("key1"));
        Assert.assertEquals("value1=value2", reg.lookup("key1", String.class));
    }
}
