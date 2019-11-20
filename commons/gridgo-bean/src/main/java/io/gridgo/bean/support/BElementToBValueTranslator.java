package io.gridgo.bean.support;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BValue;
import io.gridgo.utils.pojo.translator.RegisterValueTranslator;
import io.gridgo.utils.pojo.translator.ValueTranslator;

@RegisterValueTranslator(value = "toBValue")
public class BElementToBValueTranslator implements ValueTranslator<BElement, BValue> {

    @Override
    public BValue translate(BElement obj) {
        if (obj == null)
            return null;
        return ((BElement) obj).asValue();
    }
}
