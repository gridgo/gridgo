package io.gridgo.connector.support.config;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import io.gridgo.connector.support.MessageTransformer;
import io.gridgo.framework.execution.ExecutionStrategy;
import io.gridgo.framework.support.Message;
import io.gridgo.framework.support.Registry;
import io.gridgo.framework.support.RegistryAware;
import io.gridgo.framework.support.generators.IdGenerator;

public interface ConnectorContext extends RegistryAware {

    public ExecutionStrategy getCallbackInvokerStrategy();

    public Optional<ExecutionStrategy> getConsumerExecutionStrategy();

    public Consumer<Throwable> getExceptionHandler();

    public Optional<Function<Throwable, Message>> getFailureHandler();

    public IdGenerator getIdGenerator();

    public Optional<ExecutionStrategy> getProducerExecutionStrategy();

    public Registry getRegistry();

    public MessageTransformer getSerializeTransformer();

    public MessageTransformer getDeserializeTransformer();
}
