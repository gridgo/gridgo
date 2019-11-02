package io.gridgo.bean.serialization.text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BReference;
import io.gridgo.bean.BValue;
import io.gridgo.bean.exceptions.BeanSerializationException;
import io.gridgo.bean.serialization.AbstractBSerializer;
import io.gridgo.bean.serialization.BSerializationPlugin;
import io.gridgo.utils.ByteArrayUtils;
import io.gridgo.utils.exception.RuntimeIOException;
import io.gridgo.utils.pojo.PojoJsonUtils;
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
            writeElement(element, outWriter);
            outWriter.flush();
        } catch (IOException e) {
            throw new RuntimeIOException("Error while write out json", e);
        }
    }

    private void writeElement(BElement element, Appendable outWriter) throws IOException {
        if (element.isArray()) {
            writeElement(element.asArray(), outWriter);
        } else if (element.isObject()) {
            writeElement(element.asObject(), outWriter);
        } else if (element.isValue()) {
            writeElement(element.asValue(), outWriter);
        } else if (element.isReference()) {
            writeElement(element.asReference(), outWriter);
        }
    }

    @SuppressWarnings("unchecked")
    private void writeElement(BReference element, Appendable outWriter) throws IOException {
        var jsonElement = toJsonElement(element);

        if (Collection.class.isInstance(jsonElement)) {
            JSONArray.writeJSONString((List<? extends Object>) jsonElement, outWriter);
            return;
        }

        if (Map.class.isInstance(jsonElement)) {
            JSONObject.writeJSON((Map<String, ? extends Object>) jsonElement, outWriter);
            return;
        }

        JSONValue.writeJSONString(jsonElement, outWriter);
    }

    private void writeElement(BValue element, Appendable outWriter) throws IOException {
        JSONValue.writeJSONString(toJsonElement(element), outWriter);
    }

    private void writeElement(BObject element, Appendable outWriter) throws IOException {
        JSONObject.writeJSON(toJsonElement(element), outWriter);
    }

    private void writeElement(BArray element, Appendable outWriter) throws IOException {
        JSONArray.writeJSONString(toJsonElement(element), outWriter);
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

    @SuppressWarnings("unchecked")
    public static final <T> T toJsonElement(BElement element) {
        if (element.isArray()) {
            return (T) toJsonElement(element.asArray());
        }
        if (element.isObject()) {
            return (T) toJsonElement(element.asObject());
        }
        if (element.isValue()) {
            return (T) toJsonElement(element.asValue());
        }
        if (element.isReference()) {
            return (T) toJsonElement(element.asReference());
        }
        return null;
    }

    private static final List<?> toJsonElement(BArray arr) {
        List<?> list = new LinkedList<>();
        for (BElement element : arr) {
            list.add(toJsonElement(element));
        }
        return list;
    }

    private static final Map<String, Object> toJsonElement(BObject obj) {
        Map<String, Object> map = new TreeMap<>();
        for (Entry<String, BElement> entry : obj.entrySet()) {
            map.put(entry.getKey(), toJsonElement(entry.getValue()));
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    private static final <T> T toJsonElement(BReference reference) {
        var ref = reference.getReference();
        return ref == null ? null : (T) PojoJsonUtils.toJsonElement(reference.getReference());
    }

    @SuppressWarnings("unchecked")
    private static final <T> T toJsonElement(BValue val) {
        switch (val.getType()) {
        case RAW:
            return (T) ByteArrayUtils.toHex(val.getRaw(), "0x");
        case CHAR:
            return (T) val.getString();
        default:
            return (T) val.getData();
        }
    }
}
