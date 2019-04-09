package io.gridgo.framework.test;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.framework.support.exceptions.BeanNotFoundException;
import io.gridgo.framework.support.impl.PropertiesFileRegistry;
import io.gridgo.framework.support.impl.SimpleRegistry;

public class RegistryUnitTest {

    @Test
    public void testPrimitive() {
        var registry = new SimpleRegistry().register("flag", "true");
        Assert.assertTrue(registry.lookup("flag", Boolean.class));
    }

    @Test
    public void testPropertyRegistry() {
        var classLoader = getClass().getClassLoader();
        var file = new File(classLoader.getResource("test.properties").getFile());
        var registry = new PropertiesFileRegistry(file);
        Assert.assertEquals("hello", registry.lookup("msg"));
        registry = new PropertiesFileRegistry(file.getAbsolutePath());
        Assert.assertEquals("hello", registry.lookup("msg"));
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
}
