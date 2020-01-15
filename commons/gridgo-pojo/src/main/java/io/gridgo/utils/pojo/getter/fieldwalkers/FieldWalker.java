package io.gridgo.utils.pojo.getter.fieldwalkers;

import io.gridgo.utils.pojo.getter.PojoFlattenAcceptor;
import io.gridgo.utils.pojo.getter.PojoGetterProxy;

public interface FieldWalker {

    public void walk(Object target, PojoGetterProxy proxy, PojoFlattenAcceptor walker, boolean shallowly);
}
