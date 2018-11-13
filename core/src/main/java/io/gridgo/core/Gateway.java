package io.gridgo.core;

import java.util.function.BiConsumer;

import org.joo.promise4j.Deferred;
import org.joo.promise4j.Promise;

import io.gridgo.connector.Connector;
import io.gridgo.connector.ConnectorResolver;
import io.gridgo.core.subscription.RoutingPolicy;
import io.gridgo.core.subscription.HandlerSubscription;
import io.gridgo.framework.ComponentLifecycle;
import io.gridgo.framework.support.Message;

public interface Gateway extends ComponentLifecycle {

	public Gateway attachConnector(String endpoint);

	public Gateway attachConnector(String endpoint, ConnectorResolver resolver);

	public Gateway attachConnector(Connector endpoint);

	public Gateway attachRoutingPolicy(RoutingPolicy policy);

	public HandlerSubscription subscribe(BiConsumer<Message, Deferred<Message, Exception>> handler);

	public void send(Message message);

	public Promise<Message, Exception> sendWithAck(Message message);

	public Promise<Message, Exception> call(Message message);

	public void push(Message message);

	public void callAndPush(Message message);
}
