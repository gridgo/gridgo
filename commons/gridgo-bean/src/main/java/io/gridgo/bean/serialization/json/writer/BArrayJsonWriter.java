package io.gridgo.bean.serialization.json.writer;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BElement;
import lombok.AllArgsConstructor;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONStyle;

@AllArgsConstructor
class BArrayJsonWriter implements ElementJsonWriter<BArray> {

    private final JSONStyle style;

    private final ElementJsonWriter<BElement> jsonWriter;

    @Override
    public void writeElement(BArray element, Appendable outWriter) throws IOException {
        JSONArray.writeJSONString(toJsonElement(element), outWriter, style);
    }

    @Override
    public List<?> toJsonElement(BArray arr) {
        List<Object> list = new LinkedList<>();
        for (BElement element : arr) {
            list.add(jsonWriter.toJsonElement(element));
        }
        return list;
    }
}