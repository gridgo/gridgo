package io.gridgo.bean.serialization.text;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import io.gridgo.bean.BElement;
import io.gridgo.bean.exceptions.BeanSerializationException;
import io.gridgo.bean.serialization.BSerializationPlugin;
import net.minidev.json.parser.ParseException;

@BSerializationPlugin(ImmutableJsonSerializer.NAME)
public class ImmutableJsonSerializer extends JsonSerializer {

    public static final String NAME = "jsonro";

    @Override
    public BElement deserialize(InputStream in) {
        try {
            return getFactory().wrap(deserializeToJsonElement(in));
        } catch (UnsupportedEncodingException | ParseException e) {
            throw new BeanSerializationException("Cannot deserialize json", e);
        }
    }
}
