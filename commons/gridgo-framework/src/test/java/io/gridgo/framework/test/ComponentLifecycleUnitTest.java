package io.gridgo.framework.test;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.framework.test.support.TestComponent;

public class ComponentLifecycleUnitTest {

    @Test
    public void testComponent() throws InterruptedException {
        var comp = new TestComponent();
        Assert.assertEquals(0, comp.getData());
        var t1 = new Thread(comp::start);
        var t2 = new Thread(comp::start);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        Assert.assertEquals(1, comp.getData());
        var t3 = new Thread(comp::stop);
        var t4 = new Thread(comp::stop);
        t3.start();
        t4.start();
        t3.join();
        t4.join();
        Assert.assertEquals(0, comp.getData());
    }
}
