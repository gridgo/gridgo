package io.gridgo.utils.pojo.setter;

public interface PojoSetterProxy {

    void applyValue(Object target, String fieldName, Object value);
}
