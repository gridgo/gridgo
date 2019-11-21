package io.gridgo.utils.pojo.test.support;

import io.gridgo.utils.pojo.translator.RegisterValueTranslator;
import io.gridgo.utils.pojo.translator.ValueTranslator;

@RegisterValueTranslator("toString")
public class ToStringValueTranslator implements ValueTranslator {

    @Override
    public Object translate(Object obj) {
        if (obj == null)
            return null;
        return obj.toString();
    }
}
