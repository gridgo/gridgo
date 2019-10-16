package io.gridgo.connector;

import org.joo.promise4j.Deferred;

import io.gridgo.connector.support.config.ConnectorContext;
import io.gridgo.framework.support.Message;
import io.gridgo.utils.helper.Loggable;

public interface ProducerAck extends Loggable {

    public default void ack(Deferred<Message, Exception> deferred) {
        if (deferred != null) {
            getContext().getCallbackInvokerStrategy().execute(() -> tryResolve(deferred, null));
        }
    }

    public default void ack(Deferred<Message, Exception> deferred, Exception exception) {
        if (deferred == null)
            return;

        getContext().getCallbackInvokerStrategy().execute(() -> {
            if (exception == null) {
                tryResolve(deferred, null);
            } else {
                deferred.reject(exception);
            }
        });
    }

    public default void ack(Deferred<Message, Exception> deferred, Message response) {
        if (deferred != null) {
            getContext().getCallbackInvokerStrategy().execute(() -> tryResolve(deferred, response));
        }
    }

    public default void ack(Deferred<Message, Exception> deferred, Message response, Exception exception) {
        if (exception != null)
            ack(deferred, exception);
        else
            ack(deferred, response);
    }

    private Deferred<Message, Exception> tryResolve(Deferred<Message, Exception> deferred, Message response) {
        try {
            return deferred.resolve(response);
        } catch (Exception ex) {
            getLogger().error("Exception caught while trying to resolve deferred", ex);
            return deferred.reject(ex);
        }
    }

    public ConnectorContext getContext();
}
