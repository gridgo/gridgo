package io.gridgo.connector;

import org.joo.promise4j.Deferred;

import io.gridgo.framework.ComponentLifecycle;
import io.gridgo.framework.support.Message;
import lombok.NonNull;

/**
 * Represents a consumer. Consumers are used for receiving messages from the
 * endpoint.
 */
public interface Consumer extends ComponentLifecycle {

    /**
     * Clear all subscribers.
     */
    public void clearSubscribers();

    /**
     * Subscribe a message handler. This method is similar to
     * <code>subscribe(Consumer)</code> but allow the handler to complete (either
     * resolve or reject) the deferred. Some consumers might require at least one
     * subscriber to complete the deferred to proceed.
     * 
     * @param subscriber the handler
     * @return the consumer itself
     */
    public Consumer subscribe(final @NonNull java.util.function.BiConsumer<Message, Deferred<Message, Exception>> subscriber);

    /**
     * Subscribe a message handler. It will be called whenever a new message
     * arrives.
     * 
     * @param subscriber the handler
     * @return the consumer itself
     */
    public default Consumer subscribe(final @NonNull java.util.function.Consumer<Message> subscriber) {
        return subscribe((msg, deferred) -> subscriber.accept(msg));
    }
}
