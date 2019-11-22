package io.gridgo.utils.pojo.setter.data;

import io.gridgo.utils.PrimitiveUtils;

public interface PrimitiveData extends GenericData {

    @Override
    default boolean isPrimitive() {
        return true;
    }

    @Override
    default boolean isNull() {
        return getData() == null;
    }

    Object getData();

    default Class<?> getValueClass() {
        var data = getData();
        return data == null ? null : data.getClass();
    }

    default <T> T getDataAs(Class<T> type) {
        var data = getData();
        return data == null ? null : PrimitiveUtils.getValueFrom(type, data);
    }

    @Override
    default Object getInnerValue() {
        return this.getData();
    }
}
