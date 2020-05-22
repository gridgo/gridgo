package io.gridgo.utils.pojo.setter.fieldconverters;

import io.gridgo.utils.pojo.PojoMethodSignature;
import io.gridgo.utils.pojo.setter.data.GenericData;

public interface FieldConverter<T extends GenericData> {

    public Object convert(T data, PojoMethodSignature signature);
}
