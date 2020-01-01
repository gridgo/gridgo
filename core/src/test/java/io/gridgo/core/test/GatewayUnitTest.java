package io.gridgo.core.test;

import org.joo.libra.sql.SqlPredicate;
import org.joo.promise4j.PromiseException;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import io.gridgo.bean.BObject;
import io.gridgo.bean.BValue;
import io.gridgo.connector.Connector;
import io.gridgo.connector.impl.resolvers.ClasspathConnectorResolver;
import io.gridgo.connector.test.TestConsumer;
import io.gridgo.core.GridgoContext;
import io.gridgo.core.impl.DefaultGridgoContextBuilder;
import io.gridgo.core.support.ProducerJoinMode;
import io.gridgo.core.support.impl.SwitchComponent;
import io.gridgo.core.support.subscription.RoutingPolicy;
import io.gridgo.core.support.subscription.impl.DefaultRoutingPolicy;
import io.gridgo.core.support.template.impl.MatchingProducerTemplate;
import io.gridgo.core.support.transformers.impl.FormattedDeserializeMessageTransformer;
import io.gridgo.core.support.transformers.impl.FormattedSerializeMessageTransformer;
import io.gridgo.framework.support.Message;
import io.gridgo.framework.support.Payload;
import io.gridgo.framework.support.impl.SimpleRegistry;

public class GatewayUnitTest {

    private static final int NUM_MESSAGES = 100;

    @Test
    public void testNoSupportedProducer() throws InterruptedException {
        var latch = new CountDownLatch(2);
        var context = new DefaultGridgoContextBuilder().setName("test").build();

        context.openGateway("test").attachConnector("empty");

        context.openGateway("test2");

        context.start();

        context.findGateway("test").get().call(createType1Message()).done(response -> {
            if (response == null)
                latch.countDown();
        });

        context.findGateway("test2").get().call(createType1Message()).done(response -> {
            if (response == null)
                latch.countDown();
        });

        latch.await();

        context.stop();
    }

    @Test
    public void testSwitch() throws InterruptedException {
        var latch = new CountDownLatch(2);

        var registry = new SimpleRegistry().register("dummy", 1);
        var context = new DefaultGridgoContextBuilder().setName("test").setRegistry(registry).build();

        context.openGateway("test") //
               .attachConnector("test99:dummy").finishAttaching() //
               .subscribe((rc, gc) -> {
                   latch.countDown();
               });

        context.openGateway("test2") //
               .attachConnector("test99:dummy").finishAttaching() //
               .subscribe((rc, gc) -> {
                   latch.countDown();
               });

        context.attachComponent(new SwitchComponent("test2", "test"));

        context.start();

        context.findGateway("test2").get().push(createType1Message());

        latch.await();

        context.stop();
    }

    @Test
    public void testCallAndPush() throws InterruptedException {
        var latch1 = new CountDownLatch(1);
        var latch2 = new CountDownLatch(1);

        var registry = new SimpleRegistry().register("dummy1", 1).register("dummy2", 2);
        var context = new DefaultGridgoContextBuilder().setName("test").setRegistry(registry).build();

        RoutingPolicy policy1 = new DefaultRoutingPolicy(null).setCondition(new SqlPredicate("payload.body.data == 1"));
        RoutingPolicy policy2 = new DefaultRoutingPolicy(null).setCondition(new SqlPredicate("payload.body.data == 2"));
        context.openGateway("test2", new MatchingProducerTemplate((c, m) -> match(context, c, m))) //
               .attachConnector("test99:dummy1", new ClasspathConnectorResolver("io.gridgo.connector.test")).finishAttaching() //
               .attachConnector("test99:dummy2", new ClasspathConnectorResolver("io.gridgo.connector.test")).finishAttaching() //
               .subscribe((rc, gc) -> {
                   latch1.countDown();
               }).withPolicy(policy1) //
               .subscribe((rc, gc) -> {
                   latch2.countDown();
               }).withPolicy(policy2);

        context.start();

        var gateway2 = context.findGateway("test2").orElseThrow();
        gateway2.callAndPush(createType1Message());
        gateway2.callAndPush(createType2Message());

        latch1.await();
        latch2.await();

        context.stop();
    }

    @Test
    public void testProducerSerializeTransformer() throws InterruptedException, PromiseException {
        var context = new DefaultGridgoContextBuilder().setName("test").build();
        context.openGateway("test", ProducerJoinMode.SINGLE) //
               .attachConnector("testtransformer")
               .transformOutgoingWith(new FormattedSerializeMessageTransformer("json"));
        context.start();
        var gateway = context.findGatewayMandatory("test");

        var result = gateway.callAny(BObject.of("name", "test")).get();
        Assert.assertTrue(result != null && result.body() != null && result.body().isObject());
        Assert.assertEquals("test", result.body().asObject().getString("reply"));

        result = gateway.sendAnyWithAck(BObject.of("name", "test")).get();
        Assert.assertTrue(result != null && result.body() != null && result.body().isObject());
        Assert.assertEquals("test", result.body().asObject().getString("reply"));

        gateway.sendAny(BObject.of("name", "test"));
    }

    @Test
    public void testProducerDeserializeq1Transformer() throws InterruptedException, PromiseException {
        var context = new DefaultGridgoContextBuilder().setName("test").build();
        context.openGateway("test", ProducerJoinMode.SINGLE) //
               .attachConnector("testtransformer")
               .transformIncomingWith(this::transformProducerDeserialize);
        context.start();
        var gateway = context.findGatewayMandatory("test");

        var result = gateway.callAny(BObject.of("name", "test").toJson()).get();
        Assert.assertTrue(result != null && result.body() != null && result.body().isObject());
        Assert.assertEquals("test", result.body().asObject().getString("loop"));

        result = gateway.sendAnyWithAck(BObject.of("name", "test").toJson()).get();
        Assert.assertTrue(result != null && result.body() != null && result.body().isObject());
        Assert.assertEquals("test", result.body().asObject().getString("loop"));

        gateway.sendAny(BObject.of("name", "test").toJson());
    }

    private Message transformProducerDeserialize(Message msg) {
        if (!msg.body().isObject())
            throw new RuntimeException("Object expected");
        return Message.ofAny(BObject.of("loop", msg.body().asObject().getString("reply")));
    }

    @Test
    public void testProducerTemplate() throws InterruptedException {
        var registry = new SimpleRegistry().register("dummy1", 1).register("dummy2", 2);
        var context = new DefaultGridgoContextBuilder().setName("test").setRegistry(registry).build();

        context.openGateway("test", ProducerJoinMode.JOIN) //
               .attachConnector("test99:dummy1").finishAttaching() //
               .attachConnector("test99:dummy2");
        context.openGateway("test2", new MatchingProducerTemplate((c, m) -> match(context, c, m))) //
               .attachConnector("test99:dummy1").finishAttaching() //
               .attachConnector("test99:dummy2");

        context.start();

        var latch = new CountDownLatch(1);

        var gateway = context.findGateway("test").orElseThrow();
        gateway.call(createType1Message()).done(response -> {
            var body = response.body();
            Assert.assertTrue(body.isArray());
            Assert.assertEquals(2, body.asArray().size());
            Assert.assertEquals(Integer.valueOf(1), body.asArray().getArray(0).getInteger(2));
            Assert.assertEquals(Integer.valueOf(2), body.asArray().getArray(1).getInteger(2));
            latch.countDown();
        }).fail(ex -> ex.printStackTrace());

        latch.await();

        var latch2 = new CountDownLatch(2);

        var gateway2 = context.findGateway("test2").orElseThrow();
        gateway2.call(createType1Message()).done(response -> {
            var body = response.body();
            Assert.assertTrue(body.isArray());
            Assert.assertEquals(1, body.asArray().size());
            Assert.assertEquals(Integer.valueOf(1), body.asArray().getArray(0).getInteger(2));
            latch2.countDown();
        }).fail(ex -> ex.printStackTrace());
        gateway2.call(createType2Message()).done(response -> {
            var body = response.body();
            Assert.assertTrue(body.isArray());
            Assert.assertEquals(1, body.asArray().size());
            Assert.assertEquals(Integer.valueOf(2), body.asArray().getArray(0).getInteger(2));
            latch2.countDown();
        }).fail(ex -> ex.printStackTrace());

        latch.await();

        gateway.send(createType1Message());
        gateway2.send(createType1Message());

        context.stop();
    }

    private boolean match(GridgoContext context, Connector connector, Message msg) {
        var beanName = connector.getConnectorConfig().getPlaceholders().getProperty("bean");
        var beanValue = context.getRegistry().lookupMandatory(beanName, Integer.class);
        return beanValue == msg.body().asValue().getInteger();
    }

    @Test
    public void testConnector() throws InterruptedException {
        var beanValue = 1;
        var registry = new SimpleRegistry().register("dummy", beanValue).register("dummy1", 2);
        var context = new DefaultGridgoContextBuilder().setName("test").setRegistry(registry).build();

        var consumerLatch = new CountDownLatch(2);

        RoutingPolicy policy = new DefaultRoutingPolicy((rc, gc) -> {
            consumerLatch.countDown();
        }).setCondition(new SqlPredicate("payload.body.data == " + beanValue));
        context.openGateway("test") //
               .attachConnector("test99:dummy").finishAttaching() //
               .attachConnector("test99:dummy1").finishAttaching() //
               .attachRoutingPolicy(policy);
        context.start();

        context.findGateway("test").get().push(createType1Message());
        context.findGateway("test").get().push(createType1Message());
        context.findGateway("test").get().push(createType2Message());
        context.findGateway("test").get().push(createType2Message());

        consumerLatch.await();

        var latch = new CountDownLatch(2);

        var gateway = context.findGateway("test").orElseThrow();
        gateway.call(createType1Message()).done(response -> {
            if (response.body().asValue().getInteger() == beanValue)
                latch.countDown();
        });
        gateway.sendWithAck(createType1Message()).done(response -> {
            Assert.assertNull(response);
            latch.countDown();
        });

        latch.await();

        context.stop();
    }

    @Test
    public void testPush() throws InterruptedException {
        var latch1 = new CountDownLatch(NUM_MESSAGES / 2);
        var latch2 = new CountDownLatch(NUM_MESSAGES / 2);
        var registry = new SimpleRegistry().register("dummy", 1);
        var context = new DefaultGridgoContextBuilder().setName("test").setRegistry(registry).build();
        var firstGateway = context.openGateway("test") //
                                  .subscribe((rc, gc) -> {
                                      latch1.countDown();
                                  }) //
                                  .when("payload.body.data == 1").finishSubscribing()//
                                  .subscribe((rc, gc) -> {
                                      latch2.countDown();
                                  }) //
                                  .when("payload.body.data == 2").finishSubscribing();

        context.start();

        var gateway = context.findGateway("test").orElseThrow();
        Assert.assertEquals(firstGateway, gateway);

        for (int i = 0; i < NUM_MESSAGES / 2; i++)
            gateway.push(createType1Message());
        for (int i = 0; i < NUM_MESSAGES / 2; i++)
            gateway.push(createType2Message());

        latch1.await();
        latch2.await();

        Assert.assertEquals(firstGateway, context.openGateway("test"));

        context.closeGateway("test");
        Assert.assertTrue(context.findGateway("test").isEmpty());

        context.stop();
    }

    @Test
    public void testInstrumenter() throws InterruptedException {
        var atomic = new AtomicInteger();
        var latch1 = new CountDownLatch(2);
        var context = new DefaultGridgoContextBuilder().setName("test").build();
        context.openGateway("test") //
               .subscribe((rc, gc) -> {
                   latch1.countDown();
               }) //
               .when("payload.body.data == 1") //
               .instrumentWith((msg, deferred, runnable) -> (() -> {
                   atomic.incrementAndGet();
                   runnable.run();
               })) //
               .finishSubscribing() //
               .subscribe((rc, gc) -> {
                   latch1.countDown();
               }) //
               .when("payload.body.data == 2") //
               .instrumentWith((msg, deferred, runnable) -> (() -> {
                   runnable.run();
               }));

        context.start();

        var gateway = context.findGateway("test").orElseThrow();
        gateway.push(createType1Message());
        gateway.push(createType2Message());

        latch1.await();

        Assert.assertEquals(1, atomic.get());

        context.stop();
    }

    private Message createType2Message() {
        return Message.of(Payload.of(BValue.of(2)));
    }

    private Message createType1Message() {
        return Message.of(Payload.of(BValue.of(1)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConsumerDeserializeTransformerWrongType() throws InterruptedException, PromiseException {
        var ref = new AtomicReference<>();
        var context = new DefaultGridgoContextBuilder().setName("test").build();
        context.openGateway("test") //
               .attachConnector("testtransformer:")
               .transformIncomingWith(new FormattedDeserializeMessageTransformer("json"))
               .finishAttaching()
               .subscribe((rc, gc) -> {
                   var msg = rc.getMessage();
                   var deferred = rc.getDeferred();
                   if (msg.body() == null)
                       ref.set(null);
                   else
                       ref.set(msg.body().asObject().getString("name"));
                   deferred.resolve(null);
               });
        context.start();
        var connector = context.findGatewayMandatory("test").getConnectorAttachments().get(0).getConnector();
        var consumer = (TestConsumer) connector.getConsumer().orElseThrow();
        consumer.subscribe(msg -> {});
        try {
            consumer.testPublish(Message.ofAny(BObject.of("name", "test"))).get();
        } catch (PromiseException e) {
            if (e.getCause() != null && e.getCause() instanceof RuntimeException)
                throw (RuntimeException) e.getCause();
            throw e;
        }
    }

    @Test
    public void testConsumerDeserializeTransformer() throws PromiseException, InterruptedException {
        var ref = new AtomicReference<>();
        var context = new DefaultGridgoContextBuilder().setName("test").build();
        context.openGateway("test") //
               .attachConnector("testtransformer:")
               .transformIncomingWith(new FormattedDeserializeMessageTransformer("json"))
               .finishAttaching()
               .subscribe((rc, gc) -> {
                   var msg = rc.getMessage();
                   var deferred = rc.getDeferred();
                   if (msg.body() == null)
                       ref.set(null);
                   else
                       ref.set(msg.body().asObject().getString("name"));
                   deferred.resolve(null);
               });
        context.start();
        var connector = context.findGatewayMandatory("test").getConnectorAttachments().get(0).getConnector();
        var consumer = (TestConsumer) connector.getConsumer().orElseThrow();
        consumer.testPublish(Message.ofAny("{\"name\":\"test\"}")).get();
        Assert.assertEquals("test", ref.get());
        consumer.testPublish(Message.ofEmpty()).get();
        Assert.assertNull(ref.get());
    }

    @Test
    public void testConsumerSerializeTransformer() throws PromiseException, InterruptedException {
        var context = new DefaultGridgoContextBuilder().setName("test").build();
        context.openGateway("test") //
               .attachConnector("testtransformer:")
               .transformOutgoingWith(new FormattedSerializeMessageTransformer("json"))
               .finishAttaching()
               .subscribe((rc, gc) -> {
                   rc.getDeferred().resolve(rc.getMessage());
               });
        context.start();
        var connector = context.findGatewayMandatory("test").getConnectorAttachments().get(0).getConnector();
        var consumer = (TestConsumer) connector.getConsumer().orElseThrow();
        var result = consumer.testPublish(Message.ofAny(BObject.of("name", "test"))).get();
        Assert.assertEquals("{\"name\":\"test\"}", new String(result.body().asValue().getRaw()));
        result = consumer.testPublish(Message.ofEmpty()).get();
        Assert.assertNull(result.body());
    }
}
