package io.gridgo.connector.test;

import io.gridgo.bean.BObject;
import io.gridgo.bean.BValue;
import io.gridgo.connector.impl.AbstractConsumer;
import io.gridgo.connector.support.config.ConnectorContext;

public class BeanConsumer extends AbstractConsumer {

	private Integer beanValue;

	public BeanConsumer(ConnectorContext context, Integer beanValue) {
		super(context);
		this.beanValue = beanValue;
	}

	@Override
	protected String generateName() {
		return "consumer.bean";
	}

	@Override
	protected void onStart() {
	
	}
	
	public void testPublish() {
		publish(createMessage(BObject.newDefault(), BValue.newDefault(beanValue)), null);
		publish(createMessage(BObject.newDefault(), BValue.newDefault(beanValue)), null);
	}

	@Override
	protected void onStop() {

	}

}
