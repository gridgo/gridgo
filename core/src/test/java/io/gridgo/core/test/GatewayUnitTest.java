package io.gridgo.core.test;

import java.util.concurrent.CountDownLatch;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.bean.BValue;
import io.gridgo.core.impl.DefaultGridgoContextBuilder;
import io.gridgo.framework.support.Message;
import io.gridgo.framework.support.Payload;
import io.gridgo.framework.support.impl.SimpleRegistry;

public class GatewayUnitTest {

	private static final int NUM_MESSAGES = 100;

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
				}).when("payload.body.data == 2").finishSubscribing();

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
