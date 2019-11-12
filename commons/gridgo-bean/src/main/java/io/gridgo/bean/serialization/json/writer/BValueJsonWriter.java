package io.gridgo.bean.serialization.json.writer;

import java.io.IOException;

import io.gridgo.bean.BValue;
import io.gridgo.utils.ByteArrayUtils;
import lombok.AllArgsConstructor;
import net.minidev.json.JSONStyle;
import net.minidev.json.JSONValue;

@AllArgsConstructor class BValueJsonWriter implements ElementJsonWriter<BValue> {

    private final JSONStyle style;

    @Override
    public void writeElement(BValue element, Appendable outWriter) throws IOException {
        JSONValue.writeJSONString(toJsonElement(element), outWriter, style);
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