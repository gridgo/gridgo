package io.gridgo.utils.pojo.getter.fieldwalkers;

import static io.gridgo.utils.ArrayUtils.foreachArray;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.END_ARRAY;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.START_ARRAY;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.VALUE;

import io.gridgo.utils.ArrayUtils;
import io.gridgo.utils.pojo.getter.PojoFlattenAcceptor;
import io.gridgo.utils.pojo.getter.PojoGetterProxy;

public class ArrayFieldWalker extends AbstractFieldWalker {

    private static final ArrayFieldWalker INSTANCE = new ArrayFieldWalker();

    public static ArrayFieldWalker getInstance() {
        return INSTANCE;
    }

    private ArrayFieldWalker() {
        // Nothing to do
    }

    @Override
    public void walk(Object target, PojoGetterProxy proxy, PojoFlattenAcceptor walker, boolean shallowly) {
        int length = ArrayUtils.length(target);
        walker.accept(START_ARRAY, length, null, null);
        foreachArray(target, value -> {
            if (shallowly) {
                walker.accept(VALUE, value, null, null);
            } else {
                walkRecursive(value, proxy, walker, shallowly);
            }
        });
        walker.accept(END_ARRAY, length, null, null);
    }
}
