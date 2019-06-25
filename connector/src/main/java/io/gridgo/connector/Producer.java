package io.gridgo.connector;

import io.gridgo.connector.support.MessageProducer;
import io.gridgo.connector.support.ProducerCapability;
import io.gridgo.framework.ComponentLifecycle;

/**
 * Represents a message producer. Producers are used for sending messages to the
 * endpoint.
 */
public interface Producer extends ComponentLifecycle, ProducerCapability, MessageProducer {

}
