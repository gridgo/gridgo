package io.gridgo.utils.pojo.getter;

import static io.gridgo.utils.ArrayUtils.foreachArray;
import static io.gridgo.utils.PrimitiveUtils.isPrimitive;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.END_ARRAY;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.END_MAP;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.KEY;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.KEY_NULL;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.START_ARRAY;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.START_MAP;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.VALUE;
import static lombok.AccessLevel.PRIVATE;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import io.gridgo.utils.ArrayUtils;
import io.gridgo.utils.pojo.PojoUtils;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor(access = PRIVATE)
public class PojoGetter {

    private final Object target;

    private final PojoGetterProxy proxy;

    public static PojoGetter of(@NonNull Object target, @NonNull PojoGetterProxy proxy) {
        return new PojoGetter(target, proxy);
    }

    public static PojoGetter of(@NonNull Object target) {
        return of(target, PojoGetterRegistry.DEFAULT.getGetterProxy(target.getClass()));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void walk(boolean shallowly, PojoFlattenAcceptor walker) {
        Class<?> type;

        if (target == null //
                || isPrimitive(type = target.getClass()) //
                || type == Date.class //
                || type == java.sql.Date.class) {

            walker.accept(VALUE, target);
            return;
        }

        if (type.isArray()) {
            int length = ArrayUtils.length(target);
            walker.accept(START_ARRAY, length);
            foreachArray(target, value -> {
                if (shallowly)
                    walker.accept(VALUE, value);
                else
                    PojoGetter.of(value, proxy).walk(shallowly, walker);
            });
            walker.accept(END_ARRAY, length);
            return;
        }

        if (Collection.class.isInstance(target)) {
            int length = ((Collection) target).size();
            walker.accept(START_ARRAY, length);
            var it = ((Collection) target).iterator();
            while (it.hasNext())
                if (shallowly)
                    walker.accept(VALUE, it.next());
                else
                    new PojoGetter(it.next(), proxy).walk(shallowly, walker);
            walker.accept(END_ARRAY, length);
            return;
        }

        if (Map.class.isInstance(target)) {
            var map = (Map<?, ?>) target;
            int size = map.size();
            walker.accept(START_MAP, size);
            var it = map.entrySet().iterator();
            while (it.hasNext()) {
                var entry = it.next();
                var key = entry.getKey();
                var value = entry.getValue();

                if (value == null) {
                    walker.accept(KEY_NULL, key);
                } else {
                    walker.accept(KEY, key);
                    if (shallowly)
                        walker.accept(VALUE, value);
                    else
                        PojoGetter.of(value, proxy).walk(shallowly, walker);
                }
            }
            walker.accept(END_MAP, size);
            return;
        }

        var _proxy = proxy != null ? proxy : PojoUtils.getGetterProxy(type);
        int length = _proxy.getFields().length;
        walker.accept(START_MAP, length);
        _proxy.walkThrough(target, (signature, value) -> {
            var key = signature.getTransformedOrDefaultFieldName();

            var valueTranslator = signature.getValueTranslator();
            if (valueTranslator != null && valueTranslator.translatable(value))
                value = valueTranslator.translate(value, signature);

            if (value == null) {
                walker.accept(KEY_NULL, key);
            } else {
                walker.accept(KEY, key);
                if (shallowly)
                    walker.accept(VALUE, value);
                else
                    PojoGetter.of(value, signature.getElementGetterProxy()).walk(shallowly, walker);
            }
        });
        walker.accept(END_MAP, length);
    }
}
