package io.gridgo.boot.data.support.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.joo.promise4j.Promise;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.boot.data.support.DataAccessHandler;
import io.gridgo.boot.data.support.annotations.PojoMapper;
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

    private Object toPojo(Message r, Class<?> pojo) {
        var body = r.body();
        if (body.isObject()) {
            return toPojoObject(r, pojo, body.asObject());
        } else if (body.isArray()) {
            return toPojoArray(r, pojo, body.asArray());
        } else if (body.isReference()) {
            return toPojoReference(r, pojo, body);
        }
        throw new IllegalArgumentException(String.format("Result of type %s cannot be casted to %s", //
                body.getType().name(), pojo.getClass().getName()));
    }

    private Object toPojoReference(Message r, Class<?> pojo, BElement body) {
        if (pojo.isInstance(body.asReference()))
            return body.asReference().getReference();
        throw new IllegalArgumentException(String.format("Result of type %s cannot be casted to %s", //
                body.asReference().getReference().getClass().getName(), //
                pojo.getClass().getName()));
    }

    private Object toPojoObject(Message r, Class<?> pojo, BObject body) {
        return body.toPojo(pojo);
    }

    private List<?> toPojoArray(Message r, Class<?> pojo, BArray body) {
        var list = new ArrayList<>();
        for (int i = 0; i < body.size(); i++) {
            var e = body.get(i).asObject();
            list.add(e.toPojo(pojo));
        }
        return list;
    }

    protected abstract Message buildMessage(T annotation, Object[] args);
}
