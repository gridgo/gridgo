package io.gridgo.boot.data.support.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.joo.promise4j.Promise;

import io.gridgo.boot.data.support.DataAccessHandler;
import io.gridgo.core.Gateway;
import io.gridgo.core.GridgoContext;
import io.gridgo.framework.support.Message;
import lombok.Data;

@Data
public abstract class AbstractDataAccessHandler<T extends Annotation> implements DataAccessHandler {

    protected GridgoContext context;

    protected Gateway gateway;

    private final Class<? extends T> annotatedClass;

    public AbstractDataAccessHandler(Class<? extends T> annotatedClass) {
        this.annotatedClass = annotatedClass;
    }

    @Override
    public Promise<Message, Exception> invoke(Object proxy, Method method, Object[] args) throws Throwable {
        var annotation = method.getAnnotation(annotatedClass);
        if (annotation == null) {
            return Promise.ofCause(new IllegalArgumentException(String.format("Method %s is not annotated with @%s",
                    proxy.getClass().getName(), method.getName(), annotatedClass.getSimpleName())));
        }
        var msg = buildMessage(annotation, args);
        return gateway.call(msg);
    }

    protected abstract Message buildMessage(T annotation, Object[] args);
}
