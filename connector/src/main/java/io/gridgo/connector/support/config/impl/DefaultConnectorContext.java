package io.gridgo.connector.support.config.impl;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import io.gridgo.connector.support.config.ConnectorContext;
import io.gridgo.framework.execution.ExecutionStrategy;
import io.gridgo.framework.execution.impl.DefaultExecutionStrategy;
import io.gridgo.framework.support.Message;
import io.gridgo.framework.support.Registry;
import io.gridgo.framework.support.generators.IdGenerator;
import io.gridgo.framework.support.generators.impl.NoOpIdGenerator;
import io.gridgo.framework.support.impl.SimpleRegistry;
import lombok.Getter;
import lombok.Setter;

@Getter
public class DefaultConnectorContext implements ConnectorContext {

    private static final ExecutionStrategy DEFAULT_CALLBACK_EXECUTOR = new DefaultExecutionStrategy();

    private static final java.util.function.Consumer<Throwable> DEFAULT_EXCEPTION_HANDLER = ex -> {
    };

    private IdGenerator idGenerator = new NoOpIdGenerator();

    @Setter
    private Registry registry = new SimpleRegistry();

    private Consumer<Throwable> exceptionHandler = DEFAULT_EXCEPTION_HANDLER;

    private Optional<Function<Throwable, Message>> failureHandler = Optional.empty();

    private ExecutionStrategy callbackInvokerStrategy = DEFAULT_CALLBACK_EXECUTOR;

    private Optional<ExecutionStrategy> consumerExecutionStrategy = Optional.empty();

    private Optional<ExecutionStrategy> producerExecutionStrategy = Optional.empty();

    public DefaultConnectorContext() {

    }

    protected DefaultConnectorContext(IdGenerator idGenerator, Registry registry, Consumer<Throwable> exceptionHandler,
            Function<Throwable, Message> failureHandler, ExecutionStrategy callbackInvokerStrategy, ExecutionStrategy consumerExecutionStrategy,
            ExecutionStrategy producerExecutionStrategy) {
        if (idGenerator != null)
            this.idGenerator = idGenerator;
        if (registry != null)
            this.registry = registry;
        if (exceptionHandler != null)
            this.exceptionHandler = exceptionHandler;
        if (callbackInvokerStrategy != null)
            this.callbackInvokerStrategy = callbackInvokerStrategy;
        this.consumerExecutionStrategy = Optional.ofNullable(consumerExecutionStrategy);
        this.failureHandler = Optional.ofNullable(failureHandler);
        this.producerExecutionStrategy = Optional.ofNullable(producerExecutionStrategy);
    }
}
