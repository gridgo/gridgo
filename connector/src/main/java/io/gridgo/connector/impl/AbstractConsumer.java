package io.gridgo.connector.impl;

import org.joo.promise4j.Deferred;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BValue;
import io.gridgo.connector.Consumer;
import io.gridgo.connector.support.config.ConnectorContext;
import io.gridgo.framework.support.Message;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

public abstract class AbstractConsumer extends AbstractMessageComponent implements Consumer {

    @Getter(AccessLevel.PROTECTED)
    private final Collection<BiConsumer<Message, Deferred<Message, Exception>>> subscribers = new CopyOnWriteArrayList<>();

    @Getter
    private final ConnectorContext context;

    public AbstractConsumer(ConnectorContext context) {
        this.context = context;
    }

    protected Message parseMessage(BElement data) {
        Message msg = Message.parse(data);
        ensurePayloadId(msg);
        return msg;
    }

    protected Message parseMessage(byte[] data) {
        Message msg = Message.parse(data);
        ensurePayloadId(msg);
        return msg;
    }

    @Override
    public void clearSubscribers() {
        this.subscribers.clear();
    }

    private void notifyErrors(Deferred<Message, Exception> deferred, Exception ex) {
        try {
            getLogger().error("Exception caught while publishing message", ex);
            if (deferred != null)
                deferred.reject(ex);
            getContext().getExceptionHandler().accept(ex);
        } catch (Exception e2) {
            getLogger().error("Exception caught while trying to handle exception :(", e2);
        }
    }

    private void notifySubscriber(Message message, Deferred<Message, Exception> deferred,
            BiConsumer<Message, Deferred<Message, Exception>> subscriber) {
        try {
            subscriber.accept(message, deferred);
        } catch (Exception ex) {
            notifyErrors(deferred, ex);
        }
    }

    protected boolean publish(@NonNull Message message, Deferred<Message, Exception> deferred) {
        if (this.subscribers.isEmpty())
            return false;
        for (var subscriber : this.subscribers) {
            try {
                context.getCallbackInvokerStrategy() //
                       .execute(() -> notifySubscriber(message, deferred, subscriber));
            } catch (Exception ex) {
                notifyErrors(deferred, ex);
            }
        }
        return true;
    }

    @Override
    public Consumer subscribe(BiConsumer<Message, Deferred<Message, Exception>> subscriber) {
        if (!this.subscribers.contains(subscriber)) {
            this.subscribers.add(subscriber);
        }
        return this;
    }

    @Override
    public Optional<BValue> generateId() {
        return context.getIdGenerator().generateId();
    }
}
