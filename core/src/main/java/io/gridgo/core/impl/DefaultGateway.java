package io.gridgo.core.impl;

import org.joo.promise4j.Promise;
import org.joo.promise4j.impl.CompletableDeferredObject;

import java.util.Optional;

import io.gridgo.core.GridgoContext;
import io.gridgo.core.support.ProducerJoinMode;
import io.gridgo.core.support.subscription.GatewaySubscription;
import io.gridgo.core.support.template.ProducerTemplate;
import io.gridgo.framework.execution.ProducerInstrumenter;
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

    private Optional<ProducerInstrumenter> producerInstrumenter = Optional.empty();

    public DefaultGateway(GridgoContext context, String name) {
        super(context, name);
    }

    @Override
    public void send(Message message) {
        this.producerTemplate.send(getConnectorAttachments(), message);
    }

    @Override
    public Promise<Message, Exception> sendWithAck(Message message) {
        if (producerInstrumenter.isPresent())
            return producerInstrumenter.get().instrument(message, this::doSendWithAck, getName());
        return doSendWithAck(message);
    }

    private Promise<Message, Exception> doSendWithAck(Message message) {
        return producerTemplate.sendWithAck(getConnectorAttachments(), message);
    }

    @Override
    public Promise<Message, Exception> call(Message message) {
        if (producerInstrumenter.isPresent())
            return producerInstrumenter.get().instrument(message, this::doCall, getName());
        return doCall(message);
    }

    private Promise<Message, Exception> doCall(Message message) {
        return producerTemplate.call(getConnectorAttachments(), message);
    }

    @Override
    public void callAndPush(Message message) {
        producerTemplate.call(getConnectorAttachments(), message, this::push, this::handleCallAndPushException);
    }

    @Override
    public Promise<Message, Exception> push(@NonNull Message message) {
        if (message.getSource() == null)
            throw new IllegalArgumentException("Source is required for push()");
        var deferred = new CompletableDeferredObject<Message, Exception>();
        publish(null, message, deferred);
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

    @Override
    public GatewaySubscription setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
        return this;
    }

    @Override
    public GatewaySubscription setProducerInstrumenter(ProducerInstrumenter instrumenter) {
        this.producerInstrumenter = Optional.ofNullable(instrumenter);
        return this;
    }
}
