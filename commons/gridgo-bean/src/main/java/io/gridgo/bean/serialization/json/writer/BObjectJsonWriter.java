package io.gridgo.bean.serialization.json.writer;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import lombok.AllArgsConstructor;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;

@AllArgsConstructor
class BObjectJsonWriter implements ElementJsonWriter<BObject> {

    private final JSONStyle style;

    private final ElementJsonWriter<BElement> jsonWriter;

    @Override
    public void writeElement(BObject element, Appendable outWriter) throws IOException {
        JSONObject.writeJSON(toJsonElement(element), outWriter, style);
    }

    @Override
    public Map<String, Object> toJsonElement(BObject obj) {
        Map<String, Object> map = new TreeMap<>();
        for (Entry<String, BElement> entry : obj.entrySet()) {
            map.put(entry.getKey(), jsonWriter.toJsonElement(entry.getValue()));
        }
        return map;
    }
}