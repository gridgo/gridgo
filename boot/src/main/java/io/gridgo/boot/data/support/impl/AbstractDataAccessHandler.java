package io.gridgo.boot.data.support.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.joo.promise4j.Promise;

import io.gridgo.boot.data.DataAccessHandler;
import io.gridgo.boot.data.PojoConverter;
import io.gridgo.boot.data.support.annotations.PojoMapper;
import io.gridgo.core.Gateway;
import io.gridgo.core.GridgoContext;
import io.gridgo.framework.support.Message;
import lombok.Data;

@Data
public abstract class AbstractDataAccessHandler<T extends Annotation> implements DataAccessHandler, PojoConverter {

    protected GridgoContext context;

    protected Gateway gateway;

    private final Class<? extends T> annotatedClass;

    public AbstractDataAccessHandler(Class<? extends T> annotatedClass) {
        this.annotatedClass = annotatedClass;
    }

    @Override
    public Promise<?, Exception> invoke(Object proxy, Method method, Object[] args) throws Throwable {
        var annotation = method.getAnnotation(annotatedClass);
        if (annotation == null) {
            return Promise.ofCause(new IllegalArgumentException(String.format("Method %s is not annotated with @%s",
                    proxy.getClass().getName(), method.getName(), annotatedClass.getSimpleName())));
        }
        var msg = buildMessage(annotation, args);
        return filter(method, gateway.call(msg));
    }

    protected Promise<?, Exception> filter(Method method, Promise<Message, Exception> promise) {
        var annotation = method.getAnnotation(PojoMapper.class);
        if (annotation == null)
            return promise;
        var pojo = annotation.value();
        return promise.filterDone(r -> toPojo(r, pojo));
    }

    protected abstract Message buildMessage(T annotation, Object[] args);
}
