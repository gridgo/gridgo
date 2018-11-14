package io.gridgo.core.test;

import java.util.concurrent.CountDownLatch;

import org.joo.libra.sql.SqlPredicate;
import org.junit.Assert;
import org.junit.Test;

import io.gridgo.bean.BValue;
import io.gridgo.connector.Connector;
import io.gridgo.core.GridgoContext;
import io.gridgo.core.impl.DefaultGridgoContextBuilder;
import io.gridgo.core.support.ProducerJoinMode;
import io.gridgo.core.support.subscription.RoutingPolicy;
import io.gridgo.core.support.subscription.impl.DefaultRoutingPolicy;
import io.gridgo.core.support.template.impl.MatchingProducerTemplate;
import io.gridgo.framework.support.Message;
import io.gridgo.framework.support.MessageConstants;
import io.gridgo.framework.support.Payload;
import io.gridgo.framework.support.impl.SimpleRegistry;

public class GatewayUnitTest {

	private static final int NUM_MESSAGES = 100;

	@Test
	public void testProducerTemplate() throws InterruptedException {
		var registry = new SimpleRegistry().register("dummy1", 1).register("dummy2", 2);
		var context = new DefaultGridgoContextBuilder().setName("test").setRegistry(registry).build();

		context.openGateway("test", ProducerJoinMode.JOIN) //
				.attachConnector("test:dummy1") //
				.attachConnector("test:dummy2");
		context.openGateway("test2", new MatchingProducerTemplate((c, m) -> match(context, c, m))) //
				.attachConnector("test:dummy1") //
				.attachConnector("test:dummy2");

		context.start();

		var latch = new CountDownLatch(1);

		var gateway = context.findGateway("test").orElseThrow();
		gateway.call(createType1Message()).done(response -> {
			var body = response.getPayload().getBody();
			Assert.assertTrue(body.isArray());
			Assert.assertEquals(2, body.asArray().size());
			Assert.assertEquals(1, body.asArray().getObject(0).getInteger(MessageConstants.BODY));
			Assert.assertEquals(2, body.asArray().getObject(1).getInteger(MessageConstants.BODY));
			latch.countDown();
		});

		latch.await();

		var latch2 = new CountDownLatch(2);

		var gateway2 = context.findGateway("test2").orElseThrow();
		gateway2.call(createType1Message()).done(response -> {
			var body = response.getPayload().getBody();
			Assert.assertTrue(body.isArray());
			Assert.assertEquals(1, body.asArray().size());
			Assert.assertEquals(1, body.asArray().getObject(0).getInteger(MessageConstants.BODY));
			latch2.countDown();
		});
		gateway2.call(createType2Message()).done(response -> {
			var body = response.getPayload().getBody();
			Assert.assertTrue(body.isArray());
			Assert.assertEquals(1, body.asArray().size());
			Assert.assertEquals(2, body.asArray().getObject(0).getInteger(MessageConstants.BODY));
			latch2.countDown();
		});

		latch.await();

		gateway.send(createType1Message());
		gateway2.send(createType1Message());

		context.stop();
	}

	private boolean match(GridgoContext context, Connector connector, Message msg) {
		var beanName = connector.getConnectorConfig().getPlaceholders().getProperty("bean");
		var beanValue = context.getRegistry().lookupMandatory(beanName, Integer.class);
		return beanValue == msg.getPayload().getBody().asValue().getInteger();
	}

	@Test
	public void testConnector() throws InterruptedException {
		var beanValue = 1;
		var registry = new SimpleRegistry().register("dummy", beanValue);
		var context = new DefaultGridgoContextBuilder().setName("test").setRegistry(registry).build();

		var consumerLatch = new CountDownLatch(2);

		RoutingPolicy policy = new DefaultRoutingPolicy((rc, gc) -> {
			consumerLatch.countDown();
		}).setCondition(new SqlPredicate("payload.body.data == " + beanValue));
		context.openGateway("test") //
				.attachConnector("test:dummy") //
				.attachRoutingPolicy(policy);
		context.start();

		consumerLatch.await();

		var latch = new CountDownLatch(1);

		var gateway = context.findGateway("test").orElseThrow();
		gateway.call(createType1Message()).done(response -> {
			if (response.getPayload().getBody().asValue().getInteger() == beanValue)
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
		RoutingPolicy policy = new DefaultRoutingPolicy(null).setCondition(new SqlPredicate("payload.body.data == 2"));
		var firstGateway = context.openGateway("test") //
				.subscribe((rc, gc) -> {
					latch1.countDown();
				}) //
				.when("payload.body.data == 1").finishSubscribing()//
				.subscribe((rc, gc) -> {
					latch2.countDown();
				}).withPolicy(policy);

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

	private Message createType2Message() {
		return Message.newDefault(Payload.newDefault(BValue.newDefault(2)));
	}

	private Message createType1Message() {
		return Message.newDefault(Payload.newDefault(BValue.newDefault(1)));
	}
}
