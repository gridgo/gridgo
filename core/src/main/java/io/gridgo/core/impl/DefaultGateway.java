package io.gridgo.core.impl;

import java.util.Optional;

import org.joo.promise4j.Promise;
import org.joo.promise4j.impl.CompletableDeferredObject;

import io.gridgo.core.GridgoContext;
import io.gridgo.core.support.ProducerJoinMode;
import io.gridgo.core.support.subscription.GatewaySubscription;
import io.gridgo.core.support.template.ProducerTemplate;
import io.gridgo.framework.execution.ExecutionStrategyInstrumenter;
import io.gridgo.framework.support.Message;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultGateway extends AbstractGatewaySubscription {

    @Getter
    private ProducerTemplate producerTemplate = ProducerTemplate.create(ProducerJoinMode.SINGLE);

    @Getter
    private boolean autoStart = true;

    private Optional<ExecutionStrategyInstrumenter> producerInstrumenter = Optional.empty();

    public DefaultGateway(GridgoContext context, String name) {
        super(context, name);
    }

    @Override
    public void send(Message message) {
        this.producerTemplate.send(getConnectors(), message);
    }

    @Override
    public Promise<Message, Exception> sendWithAck(Message message) {
        if (producerInstrumenter.isPresent())
            return producerInstrumenter.get().instrument(message, this::doSendWithAck);
        return doSendWithAck(message);
    }

    private Promise<Message, Exception> doSendWithAck(Message message) {
        return producerTemplate.sendWithAck(getConnectors(), message);
    }

    @Override
    public Promise<Message, Exception> call(Message message) {
        if (producerInstrumenter.isPresent())
            return producerInstrumenter.get().instrument(message, this::doCall);
        return doCall(message);
    }

    private Promise<Message, Exception> doCall(Message message) {
        return producerTemplate.call(getConnectors(), message);
    }

    @Override
    public void callAndPush(Message message) {
        producerTemplate.call(getConnectors(), message, this::push, this::handleCallAndPushException);
    }

    @Override
    public Promise<Message, Exception> push(@NonNull Message message) {
        if (message.getSource() == null)
            throw new IllegalArgumentException("Source is required for push()");
        var deferred = new CompletableDeferredObject<Message, Exception>();
        publish(message, deferred);
        return deferred.promise();
    }

    private void handleCallAndPushException(Exception ex) {
        log.error("Error caught while calling callAndPush", ex);
        getContext().getExceptionHandler().accept(ex);
    }

    @Override
    public GatewaySubscription setProducerTemplate(ProducerTemplate producerTemplate) {
        if (producerTemplate != null)
            this.producerTemplate = producerTemplate;
        return this;
    }

    public GatewaySubscription setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
        return this;
    }

    @Override
    public GatewaySubscription setProducerInstrumenter(ExecutionStrategyInstrumenter instrumenter) {
        this.producerInstrumenter = Optional.ofNullable(instrumenter);
        return this;
    }
}
