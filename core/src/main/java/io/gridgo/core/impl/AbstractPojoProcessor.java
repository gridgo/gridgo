package io.gridgo.core.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.joo.promise4j.Deferred;

import io.gridgo.bean.BElement;
import io.gridgo.core.GridgoContext;
import io.gridgo.core.support.RoutingContext;
import io.gridgo.framework.support.Message;

public abstract class AbstractPojoProcessor<T> extends AbstractProcessor {

    private Class<? extends T> pojoType;

    public AbstractPojoProcessor(Class<? extends T> pojoType) {
        this.pojoType = pojoType;
    }

    @Override
    public void process(RoutingContext rc, GridgoContext gc) {
        var body = rc.getMessage().body();
        if (body.isArray()) {
            var request = convertBodyToList(body);
            processMulti(request, rc.getDeferred(), gc);
        } else {
            var request = convertBodyToRequest(body);
            processSingle(request, rc.getDeferred(), gc);
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
        if (body.isValue())
            return (T) body.asValue().getData();
        return body.asObject().toPojo(pojoType);
    }

    protected void processSingle(T request, Deferred<Message, Exception> deferred, GridgoContext gc) {
        // To be implemented in subclasses
    }

    protected void processMulti(List<T> request, Deferred<Message, Exception> deferred, GridgoContext gc) {
        // To be implemented in subclasses
    }
}
