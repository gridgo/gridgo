package io.gridgo.bean.serialization.text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import io.gridgo.bean.BElement;
import io.gridgo.bean.exceptions.BeanSerializationException;
import io.gridgo.bean.serialization.AbstractBSerializer;
import io.gridgo.bean.serialization.BDeserializationConfig;
import io.gridgo.bean.serialization.BSerializationPlugin;
import io.gridgo.utils.ArrayUtils;
import io.gridgo.utils.PrimitiveUtils;
import io.gridgo.utils.exception.RuntimeIOException;
import io.gridgo.utils.pojo.PojoUtils;
import io.gridgo.utils.pojo.setter.PojoSetterProxy;
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

    private void writeAny(Object obj, Appendable outWriter) throws IOException {
        if (obj instanceof BElement) {
            BElement element = (BElement) obj;
            if (element.isArray()) {
                JSONArray.writeJSONString(element.asArray().toJsonElement(), outWriter);
            } else if (element.isObject()) {
                JSONObject.writeJSON(element.asObject().toJsonElement(), outWriter);
            } else if (element.isReference()) {
                Object ref = element.asReference().getReference();
                writeAnyPojo(ref, null, outWriter);
            } else {
                JSONValue.writeJSONString(element.toJsonElement(), outWriter);
            }
        } else {
            writeAnyPojo(obj, null, outWriter);
        }
    }

    private void writeAnyPojo(Object pojo, PojoSetterProxy proxy, Appendable outWriter) throws IOException {
        Class<?> type = pojo.getClass();

        if (PrimitiveUtils.isPrimitive(type)) {
            JSONValue.writeJSONString(pojo, outWriter);
        }

        if (proxy == null) {
            proxy = PojoUtils.getSetterProxy(type);
        }
        if (ArrayUtils.isArrayOrCollection(type)) {
            outWriter.append('[');
            ArrayUtils.foreach(pojo, (ele, index, end) -> {
                
            });
        }
    }

    @Override
    public BElement deserialize(@NonNull InputStream in, BDeserializationConfig config) {
        try {
            return getFactory().fromAny(new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(in));
        } catch (UnsupportedEncodingException | ParseException e) {
            throw new BeanSerializationException("Cannot parse json");
        }
    }

}
