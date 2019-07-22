package io.gridgo.utils.pojo.test.support;

import io.gridgo.utils.pojo.setter.PojoSetter;

public class CharSetter implements PojoSetter {

    @Override
    public void set(Object target, Object value) {
        ((PrimitiveVO) target).setCharValue((char) value);
    }
}
