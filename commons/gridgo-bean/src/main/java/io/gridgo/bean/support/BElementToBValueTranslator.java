package io.gridgo.bean.support;

import io.gridgo.bean.BElement;
import io.gridgo.utils.pojo.translator.RegisterValueTranslator;
import io.gridgo.utils.pojo.translator.ValueTranslator;

@RegisterValueTranslator("toBValue")
public class BElementToBValueTranslator implements ValueTranslator {

    @Override
    public Object translate(Object obj) {
        if (obj == null)
            return null;
        return ((BElement) obj).asValue();
    }
}
