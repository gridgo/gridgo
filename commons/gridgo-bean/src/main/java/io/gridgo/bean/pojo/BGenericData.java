package io.gridgo.bean.pojo;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BReference;
import io.gridgo.bean.BValue;
import io.gridgo.utils.pojo.setter.data.GenericData;
import io.gridgo.utils.pojo.setter.data.KeyValueData;
import io.gridgo.utils.pojo.setter.data.PrimitiveData;
import io.gridgo.utils.pojo.setter.data.ReferenceData;
import io.gridgo.utils.pojo.setter.data.SequenceData;

public abstract class BGenericData implements GenericData {

    public static KeyValueData ofObject(BObject bObject) {
        return new BKeyValueData(bObject);
    }

    public static SequenceData ofArray(BArray array) {
        return new BSequenceData(array);
    }

    public static PrimitiveData ofValue(BValue array) {
        return new BPrimitiveData(array);
    }

    public static ReferenceData ofReference(BReference reference) {
        return new BReferenceData(reference);
    }

    public static GenericData ofAny(BElement element) {
        if (element == null)
            return null;

        if (element.isObject())
            return ofObject(element.asObject());

        if (element.isArray())
            return ofArray(element.asArray());

        if (element.isValue())
            return ofValue(element.asValue());

        if (element.isReference())
            return ofReference(element.asReference());

        throw new IllegalArgumentException("cannot create generic data from element: " + element);
    }

    public abstract BElement getBElement();
}
