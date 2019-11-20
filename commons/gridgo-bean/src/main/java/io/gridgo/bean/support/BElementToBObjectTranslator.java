package io.gridgo.bean.support;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.utils.pojo.translator.RegisterValueTranslator;
import io.gridgo.utils.pojo.translator.ValueTranslator;

@RegisterValueTranslator(value = "toBObject")
public class BElementToBObjectTranslator implements ValueTranslator<BElement, BObject> {

    @Override
    public BObject translate(BElement obj) {
        if (obj == null)
            return null;
        return ((BElement) obj).asObject();
    }
}
