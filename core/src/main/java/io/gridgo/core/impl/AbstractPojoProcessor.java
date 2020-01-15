package io.gridgo.core.impl;

import org.joo.promise4j.Deferred;

import java.util.List;
import java.util.stream.Collectors;

import io.gridgo.bean.BElement;
import io.gridgo.core.GridgoContext;
import io.gridgo.core.support.RoutingContext;
import io.gridgo.core.support.exceptions.SerializationException;
import io.gridgo.framework.support.Message;
import io.gridgo.utils.pojo.PojoUtils;
import io.gridgo.utils.pojo.setter.PojoSetterProxy;

public abstract class AbstractPojoProcessor<T> extends AbstractProcessor {

    private Class<? extends T> pojoType;

    private PojoSetterProxy pojoSetterProxy;

    public AbstractPojoProcessor(Class<? extends T> pojoType) {
        this.pojoType = pojoType;
        this.pojoSetterProxy = PojoUtils.getSetterProxy(pojoType);
    }

    @Override
    public void process(RoutingContext rc, GridgoContext gc) {
        var msg = rc.getMessage();
        var body = msg != null ? msg.body() : null;
        if (body != null && body.isArray()) {
            var request = convertBodyToList(body);
            processMulti(request, rc.getMessage(), rc.getDeferred(), gc);
        } else {
            var request = convertBodyToRequest(body);
            processSingle(request, rc.getMessage(), rc.getDeferred(), gc);
        }
    }

    private List<T> convertBodyToList(BElement body) {
        return body.asArray().stream() //
                   .map(this::convertBodyToRequest) //
                   .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private T convertBodyToRequest(BElement body) {
        if (body == null || body.isNullValue())
            return null;
        if (body.isReference())
            return (T) body.asReference().getReference();
        if (body.isValue())
            return (T) body.asValue().getData();
        try {
            return body.asObject().toPojo(pojoType, pojoSetterProxy);
        } catch (Exception ex) {
            return handleDeserializationException(ex, body);
        }
    }

    protected T handleDeserializationException(Exception ex, BElement body) {
        throw new SerializationException(ex);
    }

    protected void processSingle(T request, Message msg, Deferred<Message, Exception> deferred, GridgoContext gc) {
        // To be implemented in subclasses
    }

    protected void processMulti(List<T> request, Message msg, Deferred<Message, Exception> deferred, GridgoContext gc) {
        // To be implemented in subclasses
    }
}
