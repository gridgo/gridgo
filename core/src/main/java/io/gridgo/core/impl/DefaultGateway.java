package io.gridgo.core.impl;

import org.joo.promise4j.Promise;

import io.gridgo.core.Gateway;
import io.gridgo.core.GridgoContext;
import io.gridgo.core.support.ProducerJoinMode;
import io.gridgo.core.support.template.ProducerTemplate;
import io.gridgo.framework.support.Message;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultGateway extends AbstractGatewaySubscription {

	@Getter
	private ProducerTemplate producerTemplate = ProducerTemplate.create(ProducerJoinMode.SINGLE);

	@Getter
	private boolean autoStart = true;

	public DefaultGateway(GridgoContext context, String name) {
		super(context, name);
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
	public void callAndPush(Message message) {
		producerTemplate.call(getConnectors(), message, this::push, this::handleCallAndPushException);
	}

	@Override
	public void push(Message message) {
		publish(message, null);
	}

	private void handleCallAndPushException(Exception ex) {
		log.error("Error caught while calling callAndPush", ex);
		getContext().getExceptionHandler().accept(ex);
	}

	@Override
	public Gateway setProducerTemplate(ProducerTemplate producerTemplate) {
		if (producerTemplate != null)
			this.producerTemplate = producerTemplate;
		return this;
	}
	
	public Gateway setAutoStart(boolean autoStart) {
		this.autoStart = autoStart;
		return this;
	}
}
