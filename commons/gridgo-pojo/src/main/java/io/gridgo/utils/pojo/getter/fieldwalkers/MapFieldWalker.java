package io.gridgo.utils.pojo.getter.fieldwalkers;

import java.util.Map;

import static io.gridgo.utils.pojo.PojoFlattenIndicator.END_MAP;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.KEY;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.KEY_NULL;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.START_MAP;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.VALUE;

import io.gridgo.utils.pojo.getter.PojoFlattenAcceptor;
import io.gridgo.utils.pojo.getter.PojoGetterProxy;

public class MapFieldWalker extends AbstractFieldWalker {

    private static final MapFieldWalker INSTANCE = new MapFieldWalker();

    public static MapFieldWalker getInstance() {
        return INSTANCE;
    }

    private MapFieldWalker() {
        // Nothing to do
    }

    @Override
    public void walk(Object target, PojoGetterProxy proxy, PojoFlattenAcceptor walker, boolean shallowly) {
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
                if (shallowly) {
                    walker.accept(VALUE, value, null, proxy);
                } else {
                    walkRecursive(value, proxy, walker, shallowly);
                }
            }
        }
        walker.accept(END_MAP, size, null, null);
    }
}
