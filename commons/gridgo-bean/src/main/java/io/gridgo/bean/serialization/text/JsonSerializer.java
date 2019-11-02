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

    private ElementJsonWriter<BElement> jsonWriter = new CompositeJsonWriter();

    @Override
    public void serialize(@NonNull BElement element, @NonNull OutputStream out) {
        var outWriter = new OutputStreamWriter(out);
        try {
            jsonWriter.writeElement(element, outWriter);
            outWriter.flush();
        } catch (IOException e) {
            throw new RuntimeIOException("Error while write out json", e);
        }
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

    interface ElementJsonWriter<T extends BElement> {

        public void writeElement(T element, Appendable outWriter) throws IOException;

        public Object toJsonElement(T element);
    }

    static class CompositeJsonWriter implements ElementJsonWriter<BElement> {

        private static final ElementJsonWriter<BElement> INSTANCE = new CompositeJsonWriter();

        public static final ElementJsonWriter<BElement> getInstance() {
            return INSTANCE;
        }

        private ElementJsonWriter<BObject> objectJsonWriter = new BObjectJsonWriter();

        private ElementJsonWriter<BArray> arrayJsonWriter = new BArrayJsonWriter();

        private ElementJsonWriter<BValue> valueJsonWriter = new BValueJsonWriter();

        private ElementJsonWriter<BReference> refJsonWriter = new BReferenceJsonWriter();

        private CompositeJsonWriter() {

        }

        @Override
        public void writeElement(BElement element, Appendable outWriter) throws IOException {
            if (element.isArray()) {
                arrayJsonWriter.writeElement(element.asArray(), outWriter);
            } else if (element.isObject()) {
                objectJsonWriter.writeElement(element.asObject(), outWriter);
            } else if (element.isValue()) {
                valueJsonWriter.writeElement(element.asValue(), outWriter);
            } else if (element.isReference()) {
                refJsonWriter.writeElement(element.asReference(), outWriter);
            }
        }

        @Override
        public Object toJsonElement(BElement element) {
            if (element.isArray()) {
                return arrayJsonWriter.toJsonElement(element.asArray());
            }
            if (element.isObject()) {
                return objectJsonWriter.toJsonElement(element.asObject());
            }
            if (element.isValue()) {
                return valueJsonWriter.toJsonElement(element.asValue());
            }
            if (element.isReference()) {
                return refJsonWriter.toJsonElement(element.asReference());
            }
            return null;
        }

    }

    static class BArrayJsonWriter implements ElementJsonWriter<BArray> {

        @Override
        public void writeElement(BArray element, Appendable outWriter) throws IOException {
            JSONArray.writeJSONString(toJsonElement(element), outWriter);
        }

        @Override
        public List<?> toJsonElement(BArray arr) {
            List<Object> list = new LinkedList<>();
            for (BElement element : arr) {
                list.add(CompositeJsonWriter.getInstance().toJsonElement(element));
            }
            return list;
        }
    }

    static class BObjectJsonWriter implements ElementJsonWriter<BObject> {

        @Override
        public void writeElement(BObject element, Appendable outWriter) throws IOException {
            JSONObject.writeJSON(toJsonElement(element), outWriter);
        }

        @Override
        public Map<String, Object> toJsonElement(BObject obj) {
            Map<String, Object> map = new TreeMap<>();
            for (Entry<String, BElement> entry : obj.entrySet()) {
                map.put(entry.getKey(), CompositeJsonWriter.getInstance().toJsonElement(entry.getValue()));
            }
            return map;
        }
    }

    static class BValueJsonWriter implements ElementJsonWriter<BValue> {

        @Override
        public void writeElement(BValue element, Appendable outWriter) throws IOException {
            JSONValue.writeJSONString(toJsonElement(element), outWriter);
        }

        @Override
        public Object toJsonElement(BValue val) {
            switch (val.getType()) {
            case RAW:
                return ByteArrayUtils.toHex(val.getRaw(), "0x");
            case CHAR:
                return val.getString();
            default:
                return val.getData();
            }
        }
    }

    static class BReferenceJsonWriter implements ElementJsonWriter<BReference> {

        @SuppressWarnings("unchecked")
        @Override
        public void writeElement(BReference element, Appendable outWriter) throws IOException {
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

        @Override
        public Object toJsonElement(BReference reference) {
            var obj = reference.getReference();
            return obj == null ? null : PojoJsonUtils.toJsonElement(obj);
        }
    }
}
