package io.gridgo.utils.pojo.getter.fieldwalkers;

import io.gridgo.utils.pojo.getter.PojoFlattenAcceptor;
import io.gridgo.utils.pojo.getter.PojoGetterProxy;

public class GenericFieldWalker extends AbstractFieldWalker {

    private static final GenericFieldWalker INSTANCE = new GenericFieldWalker();

    public static GenericFieldWalker getInstance() {
        return INSTANCE;
    }

    private GenericFieldWalker() {
        // Nothing to do
    }

    @Override
    public void walk(Object target, PojoGetterProxy proxy, PojoFlattenAcceptor walker, boolean shallowly) {
        walkRecursive(target, proxy, walker, shallowly);
    }
}
