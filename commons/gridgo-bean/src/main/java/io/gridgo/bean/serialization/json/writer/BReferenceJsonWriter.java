package io.gridgo.bean.serialization.json.writer;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import io.gridgo.bean.BReference;
import io.gridgo.utils.pojo.PojoJsonUtils;
import lombok.AllArgsConstructor;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import net.minidev.json.JSONValue;

@AllArgsConstructor
class BReferenceJsonWriter implements ElementJsonWriter<BReference> {

    private final JSONStyle style;

    @Override
    @SuppressWarnings("unchecked")
    public void writeElement(BReference element, Appendable outWriter) throws IOException {
        var jsonElement = toJsonElement(element);

        if (Collection.class.isInstance(jsonElement)) {
            JSONArray.writeJSONString((List<? extends Object>) jsonElement, outWriter, style);
            return;
        }

        if (Map.class.isInstance(jsonElement)) {
            JSONObject.writeJSON((Map<String, ? extends Object>) jsonElement, outWriter, style);
            return;
        }

        JSONValue.writeJSONString(jsonElement, outWriter, style);
    }

    @Override
    public Object toJsonElement(BReference reference) {
        var obj = reference.getReference();
        return obj == null ? null : PojoJsonUtils.toJsonElement(obj);
    }
}