package io.gridgo.bean.support;

import io.gridgo.bean.BElement;
import io.gridgo.utils.pojo.translator.RegisterValueTranslator;
import io.gridgo.utils.pojo.translator.ValueTranslator;

@RegisterValueTranslator("toBObject")
public class BElementToBObjectTranslator implements ValueTranslator {

    @Override
    public Object translate(Object obj) {
        if (obj == null)
            return null;
        return ((BElement) obj).asObject();
    }
}
