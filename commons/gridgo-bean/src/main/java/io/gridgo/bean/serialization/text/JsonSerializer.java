package io.gridgo.bean.serialization.text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static io.gridgo.bean.support.BElementPojoHelper.anyToJsonElement;

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

@BSerializationPlugin(JsonSerializer.NAME)
public class JsonSerializer extends AbstractBSerializer {

    public static final String NAME = "json";

    @Override
    public void serialize(@NonNull BElement element, @NonNull OutputStream out) {
        var outWriter = new OutputStreamWriter(out);
        try {
            writeAny(element, outWriter);
            outWriter.flush();
        } catch (IOException e) {
            throw new RuntimeIOException("Error while write out json", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void writeAny(Object obj, Appendable outWriter) throws IOException {
        if (obj instanceof BElement) {
            writeBElement(obj, outWriter);
            return;
        }

        Object jsonElement = anyToJsonElement(obj);

        if (Collection.class.isInstance(jsonElement)) {
            JSONArray.writeJSONString((List<? extends Object>) jsonElement, outWriter);
            return;
        }

        if (Map.class.isInstance(obj)) {
            JSONObject.writeJSON((Map<String, ? extends Object>) jsonElement, outWriter);
            return;
        }

        JSONValue.writeJSONString(jsonElement, outWriter);
    }

    private void writeBElement(Object obj, Appendable outWriter) throws IOException {
        BElement element = (BElement) obj;

        if (element.isArray()) {
            JSONArray.writeJSONString(element.asArray().toJsonElement(), outWriter);
            return;
        }

        if (element.isObject()) {
            JSONObject.writeJSON(element.asObject().toJsonElement(), outWriter);
            return;
        }

        if (element.isReference()) {
            writeAny(element.asReference().getReference(), outWriter);
            return;
        }

        JSONValue.writeJSONString(element.toJsonElement(), outWriter);
    }

    @Override
    public BElement deserialize(@NonNull InputStream in) {
        try {
            return getFactory().fromAny(deserializeToJsonElement(in));
        } catch (UnsupportedEncodingException | ParseException e) {
            throw new BeanSerializationException("Cannot parse json", e);
        }
    }

    protected Object deserializeToJsonElement(InputStream in) throws ParseException, UnsupportedEncodingException {
        return new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(in);
    }
}
