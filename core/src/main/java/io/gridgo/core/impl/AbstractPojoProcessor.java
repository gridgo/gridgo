package io.gridgo.core.impl;

import java.util.List;

import org.joo.promise4j.Deferred;

import io.gridgo.bean.BElement;
import io.gridgo.core.GridgoContext;
import io.gridgo.core.support.RoutingContext;
import io.gridgo.framework.support.Message;
import io.gridgo.utils.PrimitiveUtils;

public abstract class AbstractPojoProcessor<T> extends AbstractProcessor {

    private Class<? extends T> pojoType;

    private boolean isPrimitiveType;

    private boolean isArrayType;

    private boolean isCollectionType;

    public AbstractPojoProcessor(Class<? extends T> pojoType) {
        this.pojoType = pojoType;
        if (PrimitiveUtils.isPrimitive(pojoType))
            this.isPrimitiveType = true;
        else if (pojoType.isAnnotation())
            this.isArrayType = true;
        else if (List.class.isAssignableFrom(pojoType))
            this.isCollectionType = true;
    }

    @Override
    public void process(RoutingContext rc, GridgoContext gc) {
        var body = rc.getMessage().body();
        var request = convertBodyToRequest(body);
        process(request, rc.getDeferred(), gc);
    }

    @SuppressWarnings("unchecked")
    private T convertBodyToRequest(BElement body) {
        if (body == null || body.isNullValue())
            return null;
        if (isPrimitiveType)
            return (T) body.asValue().getData();
        if (isArrayType)
            return (T) body.asArray().toArray();
        if (isCollectionType)
            return (T) body.asArray().toList();
        return body.asObject().toPojo(pojoType);
    }

    public abstract void process(T request, Deferred<Message, Exception> deferred, GridgoContext gc);
}
