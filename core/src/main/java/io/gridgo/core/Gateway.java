package io.gridgo.core;

import org.joo.promise4j.Promise;

import io.gridgo.core.support.ProducerTemplateAware;
import io.gridgo.core.support.RoutingContext;
import io.gridgo.core.support.Streamable;
import io.gridgo.core.support.subscription.GatewaySubscription;
import io.gridgo.framework.ComponentLifecycle;
import io.gridgo.framework.support.Message;

public interface Gateway
		extends ComponentLifecycle, GatewaySubscription, ProducerTemplateAware<Gateway>, Streamable<RoutingContext> {

	public void send(Message message);

	public Promise<Message, Exception> sendWithAck(Message message);

	public Promise<Message, Exception> call(Message message);

	public void push(Message message);

	public void callAndPush(Message message);
}
