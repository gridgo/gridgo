package io.gridgo.boot.data;

import java.util.ArrayList;
import java.util.List;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.framework.support.Message;

public interface PojoConverter {

    public default Object toPojo(Message r, Class<?> pojo) {
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

    public default Object toPojoReference(Message r, Class<?> pojo, BElement body) {
        if (pojo.isInstance(body.asReference()))
            return body.asReference().getReference();
        throw new IllegalArgumentException(String.format("Result of type %s cannot be casted to %s", //
                body.asReference().getReference().getClass().getName(), //
                pojo.getClass().getName()));
    }

    public default Object toPojoObject(Message r, Class<?> pojo, BObject body) {
        return body.toPojo(pojo);
    }

    public default List<?> toPojoArray(Message r, Class<?> pojo, BArray body) {
        var list = new ArrayList<>();
        for (int i = 0; i < body.size(); i++) {
            var e = body.get(i).asObject();
            list.add(e.toPojo(pojo));
        }
        return list;
    }
}
