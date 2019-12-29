package io.gridgo.utils.pojo.setter.fieldconverters;

import io.gridgo.utils.pojo.setter.PojoSetter;
import io.gridgo.utils.pojo.setter.PojoSetterProxy;
import io.gridgo.utils.pojo.setter.data.KeyValueData;
import io.gridgo.utils.pojo.setter.data.PrimitiveData;
import io.gridgo.utils.pojo.setter.data.ReferenceData;

public interface GenericDataConverter {

    public default Object fromPrimitive(PrimitiveData element, Class<?> typeToCheck) {
        return element.getDataAs(typeToCheck);
    }

    public default Object fromReference(ReferenceData element) {
        return element.getReference();
    }

    public default Object fromKeyValue(KeyValueData data, Class<?> type, PojoSetterProxy proxy) {
        return PojoSetter.ofType(type, proxy).from(data).fill();
    }
}
