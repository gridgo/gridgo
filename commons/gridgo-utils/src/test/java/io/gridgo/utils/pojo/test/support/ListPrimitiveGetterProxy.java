package io.gridgo.utils.pojo.test.support;

import io.gridgo.utils.pojo.getter.PojoGetter;

public class ListPrimitiveGetterProxy implements PojoGetter {

    @Override
    public Object get(Object target) {
        return ((CollectionVO) target).getListPrimitive();
    }

}
