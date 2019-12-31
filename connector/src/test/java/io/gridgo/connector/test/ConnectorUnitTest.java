package io.gridgo.connector.test;

import org.joo.promise4j.DeferredStatus;
import org.joo.promise4j.PromiseException;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import io.gridgo.bean.BObject;
import io.gridgo.bean.BValue;
import io.gridgo.connector.impl.factories.DefaultConnectorFactory;
import io.gridgo.connector.support.config.impl.DefaultConnectorContextBuilder;
import io.gridgo.connector.support.impl.FormattedDeserializeMessageTransformer;
import io.gridgo.connector.support.impl.FormattedSerializeMessageTransformer;
import io.gridgo.connector.test.support.TestConnector;
import io.gridgo.connector.test.support.TestConsumer;
import io.gridgo.connector.test.support.TestProducer;
import io.gridgo.framework.support.Message;
import io.gridgo.framework.support.Payload;
import io.gridgo.framework.support.impl.SimpleRegistry;

public class ConnectorUnitTest {

    @Test(expected = IllegalArgumentException.class)
    public void testConsumerDeserializeTransformerWrongType() {
        var connectorContext = new DefaultConnectorContextBuilder()
                .setDeserializeTransformer(new FormattedDeserializeMessageTransformer("json"))
                .build();
        var connector = (TestConnector) new DefaultConnectorFactory().createConnector(
                "test:pull:tcp://127.0.0.1:7781",
                connectorContext);
        var consumer = (TestConsumer) connector.getConsumer().orElseThrow();
        consumer.subscribe(msg -> {});
        consumer.testPublish(Message.ofAny(BObject.of("name", "test")));
    }

    @Test
    public void testConsumerDeserializeTransformer() throws PromiseException, InterruptedException {
        var connectorContext = new DefaultConnectorContextBuilder()
                .setDeserializeTransformer(new FormattedDeserializeMessageTransformer("json"))
                .build();
        var connector = (TestConnector) new DefaultConnectorFactory().createConnector(
                "test:pull:tcp://127.0.0.1:7781",
                connectorContext);
        var consumer = (TestConsumer) connector.getConsumer().orElseThrow();
        var ref = new AtomicReference<>();
        consumer.subscribe((msg, deferred) -> {
            if (msg.body() == null)
                ref.set(null);
            else
                ref.set(msg.body().asObject().getString("name"));
            deferred.resolve(null);
        });
        consumer.testPublish(Message.ofAny("{\"name\":\"test\"}")).get();
        Assert.assertEquals("test", ref.get());
        consumer.testPublish(Message.ofEmpty()).get();
        Assert.assertNull(ref.get());
    }

    @Test
    public void testConsumerSerializeTransformer() throws PromiseException, InterruptedException {
        var connectorContext = new DefaultConnectorContextBuilder()
                .setSerializeTransformer(new FormattedSerializeMessageTransformer("json"))
                .build();
        var connector = (TestConnector) new DefaultConnectorFactory().createConnector(
                "test:pull:tcp://127.0.0.1:7781",
                connectorContext);
        var consumer = (TestConsumer) connector.getConsumer().orElseThrow();
        consumer.subscribe((msg, deferred) -> {
            deferred.resolve(msg);
        });
        var result = consumer.testPublish(Message.ofAny(BObject.of("name", "test"))).get();
        Assert.assertEquals("{\"name\":\"test\"}", new String(result.body().asValue().getRaw()));
        result = consumer.testPublish(Message.ofEmpty()).get();
        Assert.assertNull(result.body());
    }

    @Test
    public void testConsumerFailure() {
        var exRef = new AtomicReference<Throwable>();
        var connectorContext = new DefaultConnectorContextBuilder()
                .setExceptionHandler(ex -> {
                    exRef.set(ex);
                })
                .build();
        var connector = (TestConnector) new DefaultConnectorFactory().createConnector(
                "test:pull:tcp://127.0.0.1:7781?p1=v1&p2=v2",
                connectorContext);
        var consumer = (TestConsumer) connector.getConsumer().orElseThrow();
        consumer.subscribe(msg -> {
            throw new RuntimeException("test");
        });
        var promise = consumer.testPublish();
        Assert.assertEquals(DeferredStatus.REJECTED, promise.getStatus());
        Assert.assertNotNull(exRef.get());
        Assert.assertTrue(exRef.get() instanceof RuntimeException);
        Assert.assertEquals("test", exRef.get().getMessage());
    }

    @Test
    public void testConsumer() {
        var connector = (TestConnector) new DefaultConnectorFactory().createConnector("test:pull:tcp://127.0.0.1:7781?p1=v1&p2=v2");

        Assert.assertEquals("v1", connector.getParamPublic("p1"));
        Assert.assertEquals("bar", connector.getParamPublic("foo", "bar"));
        Assert.assertEquals("pull", connector.getPlaceholderPublic("type"));
        Assert.assertEquals("tcp", connector.getPlaceholderPublic("transport"));

        var consumer = (TestConsumer) connector.getConsumer().orElseThrow();
        connector.start();
        var latch = new CountDownLatch(1);
        consumer.subscribe(msg -> {
            if (msg.headers().getInteger("test-header") == 1)
                latch.countDown();
        });
        consumer.testPublish();
        try {
            latch.await();
        } catch (InterruptedException e) {

        }
        connector.stop();
    }

    @Test
    public void testMultiSchemes() {
        var connector = new DefaultConnectorFactory().createConnector("test:pull:tcp://127.0.0.1:7782?p1=v1&p2=v2");
        Assert.assertNotNull(connector);
        Assert.assertTrue(connector instanceof TestConnector);

        connector = new DefaultConnectorFactory().createConnector("test1:pull:tcp://127.0.0.1:7782?p1=v1&p2=v2");
        Assert.assertNotNull(connector);
        Assert.assertTrue(connector instanceof TestConnector);
    }

    @Test
    public void testProducer() {
        var connector = new DefaultConnectorFactory().createConnector("test:pull:tcp://127.0.0.1:7780?p1=v1&p2=v2");
        var producer = (TestProducer) connector.getProducer().orElseThrow();
        connector.start();

        producer.send(null);

        var sendLatch = new CountDownLatch(1);
        producer.sendWithAck(null).fail(ex -> {
            if ("test exception".equals(ex.getMessage()))
                sendLatch.countDown();
        });
        try {
            sendLatch.await();
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        }

        var callLatch = new CountDownLatch(1);
        producer.call(Message.of(Payload.of(BValue.of(1)))).done(response -> {
            if (response.body().asValue().getInteger() == 2)
                callLatch.countDown();
        });
        try {
            callLatch.await();
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        }

        connector.stop();
    }

    @Test
    public void testRegistrySubstitution() {
        var registry = new SimpleRegistry() //
                                           .register("host", "localhost") //
                                           .register("port", "7779") //
                                           .register("v1", "value1") //
                                           .register("v2", "value2");
        var factory = new DefaultConnectorFactory();
        factory.setRegistry(registry);

        var connector = (TestConnector) factory.createConnector("test:pull:tcp://${host}:${port}?p1=${v1}&p2=${v2}");
        Assert.assertEquals("localhost", connector.getPlaceholderPublic("host"));
        Assert.assertEquals("7779", connector.getPlaceholderPublic("port"));
        Assert.assertEquals("value1", connector.getParamPublic("p1"));
        Assert.assertEquals("value2", connector.getParamPublic("p2"));
    }
}
