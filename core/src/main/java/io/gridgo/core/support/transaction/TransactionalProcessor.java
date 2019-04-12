package io.gridgo.core.support.transaction;

import static io.gridgo.connector.support.transaction.TransactionConstants.HEADER_CREATE_TRANSACTION;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.joo.promise4j.Deferred;
import org.joo.promise4j.Promise;
import org.joo.promise4j.impl.CompletableDeferredObject;

import io.gridgo.bean.BObject;
import io.gridgo.connector.support.MessageProducer;
import io.gridgo.connector.support.transaction.Transaction;
import io.gridgo.core.GridgoContext;
import io.gridgo.core.support.ContextAwareComponent;
import io.gridgo.core.support.exceptions.TransactionInitializationException;
import io.gridgo.framework.support.Message;
import io.gridgo.utils.helper.Loggable;

/**
 * Provides utility methods for handling transaction.
 */
public interface TransactionalProcessor extends ContextAwareComponent, Loggable {

    public GridgoContext getContext();

    public default Promise<Object, Exception> withTransaction(String gateway, Consumer<Transaction> handler) {
        return createTransaction(gateway).filterDone(transaction -> {
            try {
                handler.accept(transaction);
                return Promise.of(null);
            } catch (Exception ex) {
                handleException(transaction, ex);
                return Promise.ofCause(ex);
            }
        });
    }

    public default Promise<Object, Exception> withTransaction(String gateway,
            BiConsumer<MessageProducer, Deferred<Message, Exception>> handler) {
        return createTransaction(gateway).pipeDone(transaction -> {
            var deferred = new CompletableDeferredObject<Message, Exception>();
            try {
                handleWithPromise(transaction, deferred);
                handler.accept(transaction, deferred);
                return Promise.of(null);
            } catch (Exception ex) {
                handleException(transaction, ex);
                return Promise.ofCause(ex);
            }
        });
    }

    public default Promise<Object, Exception> withTransaction(String gateway,
            Function<MessageProducer, Promise<? extends Object, Exception>> handler) {
        return createTransaction(gateway).pipeDone(transaction -> {
            try {
                var promise = handler.apply(transaction);
                handleWithPromise(transaction, promise);
                return Promise.of(null);
            } catch (Exception ex) {
                handleException(transaction, ex);
                return Promise.ofCause(ex);
            }
        });
    }

    public default Promise<Transaction, Exception> createTransaction(String gateway) {
        return getContext().findGatewayMandatory(gateway) //
                           .callAny(BObject.of(HEADER_CREATE_TRANSACTION, 1), null) //
                           .pipeDone(this::toTransaction);
    }

    private Promise<Transaction, Exception> toTransaction(Message msg) {
        if (msg == null || msg.body() == null) {
            return Promise.ofCause(
                    new TransactionInitializationException("Underlying connector doesn't support transaction"));
        }
        if (!msg.body().isReference()) {
            return Promise.ofCause(new TransactionInitializationException(
                    "Invalid response from Connector, body is not a Transaction"));
        }
        return Promise.of(msg.body().asReference().getReference());
    }

    private void handleWithPromise(Transaction transaction, Promise<? extends Object, Exception> promise) {
        promise.done(result -> transaction.commit()) //
               .fail(ex -> handleException(transaction, ex));
    }

    private void handleException(Transaction transaction, Exception ex) {
        getLogger().error("Exception caught while calling transaction", ex);
        transaction.rollback();
    }
}
