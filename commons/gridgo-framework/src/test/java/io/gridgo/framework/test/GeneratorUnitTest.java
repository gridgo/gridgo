package io.gridgo.framework.test;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.framework.support.generators.impl.NoOpIdGenerator;
import io.gridgo.framework.support.generators.impl.TimeBasedIdGenerator;
import io.gridgo.framework.support.generators.impl.UUIDGenerator;

public class GeneratorUnitTest {

    @Test
    public void testGenerator() {
        var g1 = new NoOpIdGenerator();
        Assert.assertTrue(g1.generateId().isEmpty());
        var g2 = new TimeBasedIdGenerator();
        Assert.assertTrue(g2.generateId().isPresent());
        var g3 = new UUIDGenerator();
        Assert.assertTrue(g3.generateId().isPresent());
    }
}
