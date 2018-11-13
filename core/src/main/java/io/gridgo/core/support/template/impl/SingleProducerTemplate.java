package io.gridgo.core.support.template.impl;

import java.util.List;

import org.joo.promise4j.Promise;
import org.joo.promise4j.impl.SimpleDonePromise;

import io.gridgo.connector.Connector;
import io.gridgo.framework.support.Message;

public class SingleProducerTemplate extends AbstractProducerTemplate {

	@Override
	public Promise<Message, Exception> sendWithAck(List<Connector> connectors, Message message) {
		if (connectors.isEmpty())
			return new SimpleDonePromise<>(null);
		var first = connectors.get(0);
		var promise = sendWithAck(first, message);
		for (int i = 1; i < connectors.size(); i++) {
			send(connectors.get(i), message);
		}
		return promise;
	}

	@Override
	public Promise<Message, Exception> call(List<Connector> connectors, Message message) {
		if (connectors.isEmpty())
			return new SimpleDonePromise<>(null);
		var first = connectors.get(0);
		var promise = call(first, message);
		for (int i = 1; i < connectors.size(); i++) {
			call(connectors.get(i), message);
		}
		return promise;
	}
}
