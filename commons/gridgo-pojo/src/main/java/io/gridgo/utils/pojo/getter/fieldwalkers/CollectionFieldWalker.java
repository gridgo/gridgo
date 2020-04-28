package io.gridgo.utils.pojo.getter.fieldwalkers;

import static io.gridgo.utils.pojo.PojoFlattenIndicator.END_ARRAY;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.START_ARRAY;
import static io.gridgo.utils.pojo.PojoFlattenIndicator.VALUE;

import java.util.Collection;

import io.gridgo.utils.pojo.getter.PojoFlattenAcceptor;
import io.gridgo.utils.pojo.getter.PojoGetterProxy;

public class CollectionFieldWalker extends AbstractFieldWalker {

    private static final CollectionFieldWalker INSTANCE = new CollectionFieldWalker();

    public static CollectionFieldWalker getInstance() {
        return INSTANCE;
    }

    private CollectionFieldWalker() {
        // Nothing to do
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void walk(Object target, PojoGetterProxy proxy, PojoFlattenAcceptor walker, boolean shallowly) {
        int length = ((Collection) target).size();
        walker.accept(START_ARRAY, length, null, null);
        var it = ((Collection) target).iterator();
        while (it.hasNext()) {
            var value = it.next();
            if (shallowly) {
                walker.accept(VALUE, value, null, null);
            } else {
                walkRecursive(value, proxy, walker, shallowly);
            }
        }
        walker.accept(END_ARRAY, length, null, null);
    }
}
