package io.gridgo.bean.support;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BElement;
import io.gridgo.utils.pojo.translator.RegisterValueTranslator;
import io.gridgo.utils.pojo.translator.ValueTranslator;

@RegisterValueTranslator(value = "toBArray")
public class BElementToBArrayTranslator implements ValueTranslator<BElement, BArray> {

    @Override
    public BArray translate(BElement obj) {
        if (obj == null)
            return null;
        return ((BElement) obj).asArray();
    }
}
