package io.gridgo.bean.support;

import io.gridgo.bean.BElement;
import io.gridgo.utils.pojo.translator.RegisterValueTranslator;
import io.gridgo.utils.pojo.translator.ValueTranslator;

@RegisterValueTranslator("toBArray")
public class BElementToBArrayTranslator implements ValueTranslator {

    @Override
    public Object translate(Object obj) {
        if (obj == null)
            return null;
        return ((BElement) obj).asArray();
    }
}
