package io.gridgo.boot.data.support.impl;

import java.util.ArrayList;
import java.util.List;

import org.joo.promise4j.Promise;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.boot.data.support.annotations.JdbcProduce;
import io.gridgo.framework.support.Message;
import lombok.Getter;

@Getter
public class JdbcDataAccessHandler extends AbstractDataAccessHandler<JdbcProduce> {

    public JdbcDataAccessHandler() {
        super(JdbcProduce.class);
    }

    @Override
    protected Message buildMessage(JdbcProduce annotation, Object[] args) {
        var headers = BObject.ofEmpty();
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                headers.setAny((i + 1) + "", args[i]);
            }
        }
        var query = context.getRegistry().substituteRegistriesRecursive(annotation.value());
        return Message.ofAny(headers, query);
    }

    @Override
    protected Promise<?, Exception> filter(JdbcProduce annotation, Promise<Message, Exception> promise) {
        var pojo = annotation.pojo();
        if (pojo == Object.class) {
            return promise;
        }
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
}
