package io.gridgo.connector.test;

import java.util.Optional;

import io.gridgo.connector.impl.AbstractConnector;
import io.gridgo.connector.support.annotations.ConnectorEndpoint;

@ConnectorEndpoint(scheme = "test", syntax = "{bean}")
public class BeanConnector extends AbstractConnector {

    public void onInit() {
        var beanName = getPlaceholder("bean");
        var beanValue = getContext().getRegistry().lookupMandatory(beanName, Integer.class);
        this.consumer = Optional.of(new BeanConsumer(getContext(), beanValue));
        this.producer = Optional.of(new BeanProducer(getContext(), beanValue));
    }
}
