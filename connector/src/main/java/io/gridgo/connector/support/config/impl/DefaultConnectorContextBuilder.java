package io.gridgo.connector.support.config.impl;

import java.util.function.Consumer;
import java.util.function.Function;

import io.gridgo.connector.support.config.ConnectorContext;
import io.gridgo.connector.support.config.ConnectorContextBuilder;
import io.gridgo.framework.execution.ExecutionStrategy;
import io.gridgo.framework.support.Message;
import io.gridgo.framework.support.Registry;
import io.gridgo.framework.support.generators.IdGenerator;
import lombok.NonNull;

public class DefaultConnectorContextBuilder implements ConnectorContextBuilder {

    private IdGenerator idGenerator;

    private Registry registry;

    private Consumer<Throwable> exceptionHandler;

    private Function<Throwable, Message> failureHandler;

    private ExecutionStrategy callbackInvokerStrategy;

    private ExecutionStrategy consumerExecutionStrategy;

    private ExecutionStrategy producerExecutionStrategy;

    @Override
    public ConnectorContext build() {
        return new DefaultConnectorContext(idGenerator, registry, exceptionHandler, failureHandler, callbackInvokerStrategy, consumerExecutionStrategy,
                producerExecutionStrategy);
    }

    @Override
    public ConnectorContextBuilder setCallbackInvokerStrategy(final @NonNull ExecutionStrategy strategy) {
        this.callbackInvokerStrategy = strategy;
        return this;
    }

    @Override
    public ConnectorContextBuilder setConsumerExecutionStrategy(final @NonNull ExecutionStrategy strategy) {
        this.consumerExecutionStrategy = strategy;
        return this;
    }

    @Override
    public ConnectorContextBuilder setExceptionHandler(final @NonNull Consumer<Throwable> exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    @Override
    public ConnectorContextBuilder setFailureHandler(final @NonNull Function<Throwable, Message> failureHandler) {
        this.failureHandler = failureHandler;
        return this;
    }

    @Override
    public ConnectorContextBuilder setIdGenerator(final @NonNull IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
        return this;
    }

    @Override
    public ConnectorContextBuilder setProducerExecutionStrategy(final @NonNull ExecutionStrategy strategy) {
        this.producerExecutionStrategy = strategy;
        return this;
    }

    @Override
    public ConnectorContextBuilder setRegistry(final @NonNull Registry registry) {
        this.registry = registry;
        return this;
    }
}
