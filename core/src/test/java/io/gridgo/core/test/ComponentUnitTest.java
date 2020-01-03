package io.gridgo.core.test;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import io.gridgo.bean.BElement;
import io.gridgo.core.impl.DefaultGridgoContextBuilder;
import io.gridgo.core.support.impl.ForwardComponent;
import io.gridgo.framework.support.Message;

public class ComponentUnitTest {

    @Test
    public void testForward() throws InterruptedException {
        var ref = new AtomicReference<BElement>();
        var latch = new CountDownLatch(1);
        var context = new DefaultGridgoContextBuilder().setName("test").build();
        context.attachComponent(new ForwardComponent("g1", "g2"));
        context.openGateway("g1");
        context.openGateway("g2")
               .subscribe((rc, gc) -> {
                   ref.set(rc.getMessage().body());
                   latch.countDown();
               });
        context.start();
        context.findGatewayMandatory("g1")
               .push(Message.ofAny("test"));
        latch.await(500, TimeUnit.MILLISECONDS);
        Assert.assertNotNull(ref.get());
        Assert.assertEquals("test", ref.get().getInnerValue());
    }
}
