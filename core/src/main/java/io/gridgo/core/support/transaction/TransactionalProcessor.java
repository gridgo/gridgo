package io.gridgo.core.support.transaction;

import static io.gridgo.connector.support.transaction.TransactionConstants.HEADER_CREATE_TRANSACTION;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.joo.promise4j.Deferred;
import org.joo.promise4j.Promise;
import org.joo.promise4j.PromiseException;
import org.joo.promise4j.impl.CompletableDeferredObject;

import io.gridgo.bean.BObject;
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

    public default void withTransaction(String gateway, Consumer<Transaction> handler) {
        var transaction = createTransaction(gateway);
        try {
            handler.accept(transaction);
        } catch (Exception ex) {
            handleException(transaction, ex);
        }
    }

    public default void withTransaction(String gateway, BiConsumer<Transaction, Deferred<Message, Exception>> handler) {
        var deferred = new CompletableDeferredObject<Message, Exception>();
        var transaction = createTransaction(gateway);
        try {
            handleWithPromise(transaction, deferred);
            handler.accept(transaction, deferred);
        } catch (Exception ex) {
            handleException(transaction, ex);
        }
    }

    public default void withTransaction(String gateway,
            Function<Transaction, Promise<? extends Object, Exception>> handler) {
        var transaction = createTransaction(gateway);
        try {
            var promise = handler.apply(transaction);
            handleWithPromise(transaction, promise);
        } catch (Exception ex) {
            handleException(transaction, ex);
        }
    }

    public default Transaction createTransaction(String gateway) {
        var promise = getContext().findGatewayMandatory(gateway) //
                                  .callAny(BObject.of(HEADER_CREATE_TRANSACTION, 1), null);
        if (promise == null)
            throw new TransactionInitializationException("Underlying connector doesn't support transaction");
        try {
            return (Transaction) promise.get().body().asReference().getReference();
        } catch (PromiseException | InterruptedException e) {
            throw new TransactionInitializationException("Cannot create transaction", e);
        }
    }

    private void handleWithPromise(Transaction transaction, Promise<? extends Object, Exception> promise) {
        promise.done(result -> transaction.commit()) //
               .fail(ex -> transaction.rollback());
    }

    private void handleException(Transaction transaction, Exception ex) {
        getLogger().error("Exception caught while calling transaction", ex);
        transaction.rollback();
    }
}
