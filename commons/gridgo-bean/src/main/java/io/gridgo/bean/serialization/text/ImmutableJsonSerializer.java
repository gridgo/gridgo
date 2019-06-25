package io.gridgo.bean.serialization.text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import io.gridgo.bean.BElement;
import io.gridgo.bean.exceptions.BeanSerializationException;
import io.gridgo.bean.serialization.AbstractBSerializer;
import io.gridgo.bean.serialization.BSerializationPlugin;
import io.gridgo.utils.exception.RuntimeIOException;
import lombok.NonNull;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

@BSerializationPlugin(ImmutableJsonSerializer.NAME)
public class ImmutableJsonSerializer extends AbstractBSerializer {

    public static final String NAME = "jsonro";

    @Override
    public void serialize(@NonNull BElement element, @NonNull OutputStream out) {
        try (var outWriter = new OutputStreamWriter(out)) {
            if (element.isArray()) {
                JSONArray.writeJSONString(element.asArray().toJsonElement(), outWriter);
            } else if (element.isObject()) {
                JSONObject.writeJSON(element.asObject().toJsonElement(), outWriter);
            } else {
                JSONValue.writeJSONString(element.toJsonElement(), outWriter);
            }
        } catch (IOException e) {
            throw new RuntimeIOException("Error while write out json", e);
        }
    }

    @Override
    public BElement deserialize(@NonNull InputStream in) {
        try {
            return getFactory().wrap(new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(in));
        } catch (UnsupportedEncodingException | ParseException e) {
            throw new BeanSerializationException("Cannot parse json");
        }
    }

}
