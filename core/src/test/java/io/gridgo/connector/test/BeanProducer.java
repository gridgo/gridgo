package io.gridgo.connector.test;

import org.joo.promise4j.Promise;
import org.joo.promise4j.impl.SimpleDonePromise;

import io.gridgo.bean.BObject;
import io.gridgo.bean.BValue;
import io.gridgo.connector.impl.AbstractProducer;
import io.gridgo.connector.support.config.ConnectorContext;
import io.gridgo.framework.support.Message;

public class BeanProducer extends AbstractProducer {

	private Integer beanValue;

	public BeanProducer(ConnectorContext context, Integer beanValue) {
		super(context);
		this.beanValue = beanValue;
	}

	@Override
	public void send(Message message) {

	}

	@Override
	public Promise<Message, Exception> sendWithAck(Message message) {
		return new SimpleDonePromise<>(null);
	}

	@Override
	public Promise<Message, Exception> call(Message request) {
		return new SimpleDonePromise<>(createMessage(BObject.newDefault(), BValue.newDefault(beanValue)));
	}

	@Override
	protected String generateName() {
		return "bean";
	}

	@Override
	protected void onStart() {

	}

	@Override
	protected void onStop() {

	}

}
