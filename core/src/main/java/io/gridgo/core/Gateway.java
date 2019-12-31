package io.gridgo.core;

import org.joo.promise4j.Promise;

import java.util.List;

import io.gridgo.connector.support.MessageProducer;
import io.gridgo.core.support.RoutingContext;
import io.gridgo.core.support.Streamable;
import io.gridgo.core.support.subscription.ConnectorAttachment;
import io.gridgo.framework.ComponentLifecycle;
import io.gridgo.framework.support.Message;

public interface Gateway extends ComponentLifecycle, Streamable<RoutingContext>, MessageProducer {

    /**
     * Get the list of attached connectors.
     *
     * @return the list of attached connectors
     */
    public List<ConnectorAttachment> getConnectorAttachments();

    /**
     * Push a message to the gateway incoming sink, so it will be routed the
     * subscribers. The subscriber may or may not send back a response. If there is
     * no response the promise will never be completed.
     *
     * @param message the message to be pushed
     * @return the promise
     */
    public Promise<Message, Exception> push(Message message);

    /**
     * Send a message and wait for response. The response will then be pushed into
     * the gateway incoming sink instead of returning to the caller.
     *
     * Some connectors might not support it. If there are multiple connectors
     * attached to the gateway, only the first one is actually called, the rest are
     * usual <code>send()</code>.
     *
     * @param message
     */
    public void callAndPush(Message message);

    /**
     * Check if this gateway is auto-started.
     *
     * @return true if and only if the gateway is configured to be auto-started
     */
    public boolean isAutoStart();
}
