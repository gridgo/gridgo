package io.gridgo.utils.pojo.translator.impl;

import java.util.Date;

import io.gridgo.utils.pojo.translator.RegisterValueTranslator;
import io.gridgo.utils.pojo.translator.ValueTranslator;

@RegisterValueTranslator("timestampToDate")
public class TimestampToDateTranslator implements ValueTranslator {

    @Override
    public Object translate(Object obj) {
        if (obj == null)
            return null;
        return new Date((Long) obj);
    }
}
