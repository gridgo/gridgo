package io.gridgo.core.impl;

import org.joo.promise4j.Promise;

import io.gridgo.core.GridgoContext;
import io.gridgo.core.support.context.ProducerJoinMode;
import io.gridgo.core.support.template.ProducerTemplate;
import io.gridgo.framework.support.Message;

public class DefaultGateway extends AbstractGateway {

	private ProducerTemplate producerTemplate;

	public DefaultGateway(GridgoContext context, String name) {
		this(context, name, ProducerJoinMode.SINGLE);
	}

	public DefaultGateway(GridgoContext context, String name, ProducerJoinMode joinMode) {
		super(context, name);
		this.producerTemplate = ProducerTemplate.create(joinMode);
	}

	public DefaultGateway(GridgoContext context, String name, ProducerTemplate producerTemplate) {
		super(context, name);
		this.producerTemplate = producerTemplate;
	}

	@Override
	public void send(Message message) {
		this.producerTemplate.send(getConnectors(), message);
	}

	@Override
	public Promise<Message, Exception> sendWithAck(Message message) {
		return producerTemplate.sendWithAck(getConnectors(), message);
	}

	@Override
	public Promise<Message, Exception> call(Message message) {
		return producerTemplate.call(getConnectors(), message);
	}

	@Override
	public void push(Message message) {
		
	}

	@Override
	public void callAndPush(Message message) {
		
	}
}
