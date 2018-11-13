package io.gridgo.core.support.template.impl;

import java.util.List;

import org.joo.promise4j.DoneCallback;
import org.joo.promise4j.FailCallback;
import org.joo.promise4j.Promise;
import org.joo.promise4j.impl.SimpleFailurePromise;

import io.gridgo.connector.Connector;
import io.gridgo.core.support.template.ProducerTemplate;
import io.gridgo.framework.support.Message;

public abstract class AbstractProducerTemplate implements ProducerTemplate {

	@Override
	public void send(List<Connector> connectors, Message message) {
		for (var connector : connectors) {
			send(connector, message);
		}
	}

	@Override
	public void call(List<Connector> connectors, Message message, DoneCallback<Message> doneCallback,
			FailCallback<Exception> failCallback) {
		for (var connector : connectors) {
			call(connector, message).done(doneCallback).fail(failCallback);
		}
	}

	protected Promise<Message, Exception> call(Connector connector, Message message) {
		return connector.getProducer() //
				.map(p -> p.call(message)) //
				.orElse(createProducerNotFoundPromise(connector.getName()));
	}

	protected void send(Connector connector, Message message) {
		connector.getProducer().ifPresent(p -> p.send(message));
	}

	protected Promise<Message, Exception> sendWithAck(Connector connector, Message message) {
		return connector.getProducer() //
				.map(p -> p.sendWithAck(message)) //
				.orElse(createProducerNotFoundPromise(connector.getName()));
	}

	private SimpleFailurePromise<Message, Exception> createProducerNotFoundPromise(String name) {
		return new SimpleFailurePromise<>(
				new UnsupportedOperationException("No producer found for this connector " + name));
	}
}
