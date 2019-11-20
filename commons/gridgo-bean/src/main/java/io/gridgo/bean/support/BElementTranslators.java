package io.gridgo.bean.support;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BContainer;
import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BReference;
import io.gridgo.bean.BValue;
import io.gridgo.utils.pojo.translator.RegisterValueTranslator;

public abstract class BElementTranslators {

    @RegisterValueTranslator("toBArray")
    public static BArray toBArray(BElement ele) {
        if (ele == null)
            return null;
        return ele.asArray();
    }

    @RegisterValueTranslator("toBObject")
    public static BObject toBObject(BElement ele) {
        if (ele == null)
            return null;
        return ele.asObject();
    }

    @RegisterValueTranslator("toBValue")
    public static BValue toBValue(BElement ele) {
        if (ele == null)
            return null;
        return ele.asValue();
    }

    @RegisterValueTranslator("toBReference")
    public static BReference toBReference(BElement ele) {
        if (ele == null)
            return null;
        return ele.asReference();
    }

    @RegisterValueTranslator("toBContainer")
    public static BContainer toBContainer(BElement ele) {
        if (ele == null)
            return null;
        return ele.asContainer();
    }
}
