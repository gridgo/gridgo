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

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import io.gridgo.utils.ArrayUtils;
import lombok.NonNull;

public class PojoGetter {

    public static PojoGetter of(@NonNull Object target, PojoGetterProxy proxy) {
        return new PojoGetter(target, proxy);
    }

    public static PojoGetter of(@NonNull Object target) {
        return of(target, null);
    }

    /********************************************************
     ********************* END OF STATIC ********************
     ********************************************************/

    private final Object target;

    private final PojoGetterProxy proxy;

    private boolean shallowly = false;

    @NonNull
    private PojoFlattenAcceptor walker;

    private PojoGetter(Object target, PojoGetterProxy proxy) {
        this.target = target;
        this.proxy = proxy;
    }

    public PojoGetter shallowly(boolean shallowly) {
        this.shallowly = shallowly;
        return this;
    }

    public PojoGetter walker(PojoFlattenAcceptor walker) {
        this.walker = walker;
        return this;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void walk() {
        Class<?> type;

        if (target == null //
                || isPrimitive(type = target.getClass()) //
                || type == Date.class //
                || type == java.sql.Date.class) {

            walker.accept(VALUE, target, null, null);
            return;
        }

        if (type.isArray()) {
            int length = ArrayUtils.length(target);
            walker.accept(START_ARRAY, length, null, null);
            foreachArray(target, value -> {
                if (shallowly)
                    walker.accept(VALUE, value, null, null);
                else
                    PojoGetter.of(value, proxy).shallowly(shallowly).walker(walker).walk();
            });
            walker.accept(END_ARRAY, length, null, null);
            return;
        }

        if (Collection.class.isInstance(target)) {
            int length = ((Collection) target).size();
            walker.accept(START_ARRAY, length, null, null);
            var it = ((Collection) target).iterator();
            while (it.hasNext()) {
                var value = it.next();
                if (shallowly)
                    walker.accept(VALUE, value, null, null);
                else
                    PojoGetter.of(value, proxy) //
                            .shallowly(shallowly) //
                            .walker(walker) //
                            .walk();
            }
            walker.accept(END_ARRAY, length, null, null);
            return;
        }

        if (Map.class.isInstance(target)) {
            var map = (Map<?, ?>) target;
            int size = map.size();
            walker.accept(START_MAP, size, null, null);
            var it = map.entrySet().iterator();
            while (it.hasNext()) {
                var entry = it.next();
                var key = entry.getKey();
                var value = entry.getValue();

                if (value == null) {
                    walker.accept(KEY_NULL, key, null, proxy);
                } else {
                    walker.accept(KEY, key, null, null);
                    if (shallowly)
                        walker.accept(VALUE, value, null, proxy);
                    else
                        PojoGetter.of(value, proxy) //
                                .shallowly(shallowly) //
                                .walker(walker) //
                                .walk();
                }
            }
            walker.accept(END_MAP, size, null, null);
            return;
        }

        var _proxy = proxy != null ? proxy : PojoGetterRegistry.DEFAULT.getGetterProxy(type);
        int length = _proxy.getFields().length;
        walker.accept(START_MAP, length, null, null);
        _proxy.walkThrough(target, (signature, value) -> {
            var key = signature.getTransformedOrDefaultFieldName();

            var valueTranslator = signature.getValueTranslator();
            if (valueTranslator != null && valueTranslator.translatable(value))
                value = valueTranslator.translate(value, signature);

            if (value == null) {
                walker.accept(KEY_NULL, key, signature, null);
            } else {
                walker.accept(KEY, key, null, null);
                if (shallowly)
                    walker.accept(VALUE, value, signature, signature.getGetterProxy());
                else
                    PojoGetter.of(value, signature.getGetterProxy()) //
                            .shallowly(shallowly) //
                            .walker(walker) //
                            .walk();
            }
        });
        walker.accept(END_MAP, length, null, null);
    }
}
