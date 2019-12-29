package io.gridgo.utils.pojo.getter.fieldwalkers;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import static io.gridgo.utils.PrimitiveUtils.isPrimitive;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.VALUE;

import io.gridgo.utils.pojo.getter.PojoFlattenAcceptor;
import io.gridgo.utils.pojo.getter.PojoGetterProxy;
import io.gridgo.utils.pojo.getter.PojoGetterRegistry;

public abstract class AbstractFieldWalker implements FieldWalker {

    protected void walkRecursive(Object target, PojoGetterProxy proxy, PojoFlattenAcceptor walker, boolean shallowly) {
        Class<?> type = target == null ? null : target.getClass();

        if (target == null //
                || isPrimitive(type) //
                || type == Date.class //
                || type == java.sql.Date.class) {
            walker.accept(VALUE, target, null, null);
            return;
        }

        if (type.isArray()) {
            ArrayFieldWalker.getInstance().walk(target, proxy, walker, shallowly);
            return;
        }

        if (Collection.class.isInstance(target)) {
            CollectionFieldWalker.getInstance().walk(target, proxy, walker, shallowly);
            return;
        }

        if (Map.class.isInstance(target)) {
            MapFieldWalker.getInstance().walk(target, proxy, walker, shallowly);
            return;
        }

        var _proxy = proxy != null ? proxy : PojoGetterRegistry.DEFAULT.getGetterProxy(type);
        PojoFieldWalker.getInstance().walk(target, _proxy, walker, shallowly);
    }
}
