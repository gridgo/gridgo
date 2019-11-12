package io.gridgo.bean.serialization.json.writer;

import static net.minidev.json.JSONStyle.FLAG_IGNORE_NULL;
import static net.minidev.json.JSONStyle.LT_COMPRESS;
import static net.minidev.json.JSONStyle.MAX_COMPRESS;
import static net.minidev.json.JSONStyle.NO_COMPRESS;

import java.io.IOException;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BReference;
import io.gridgo.bean.BValue;
import lombok.Getter;
import net.minidev.json.JSONStyle;

public class CompositeJsonWriter implements ElementJsonWriter<BElement> {

    private static final JSONStyle NORMAL_STYLE = new JSONStyle(FLAG_IGNORE_NULL);

    @Getter
    private static final CompositeJsonWriter normalCompactInstance = new CompositeJsonWriter(NORMAL_STYLE);

    @Getter
    private static final CompositeJsonWriter noCompactInstance = new CompositeJsonWriter(NO_COMPRESS);

    @Getter
    private static final CompositeJsonWriter maxCompactInstance = new CompositeJsonWriter(MAX_COMPRESS);

    @Getter
    private static final CompositeJsonWriter ltCompactInstance = new CompositeJsonWriter(LT_COMPRESS);

    @Getter
    private final ElementJsonWriter<BObject> objectJsonWriter;

    @Getter
    private final ElementJsonWriter<BArray> arrayJsonWriter;

    @Getter
    private final ElementJsonWriter<BValue> valueJsonWriter;

    @Getter
    private final ElementJsonWriter<BReference> refJsonWriter;

    private CompositeJsonWriter(JSONStyle style) {
        this.objectJsonWriter = new BObjectJsonWriter(style, this);
        this.arrayJsonWriter = new BArrayJsonWriter(style, this);
        this.refJsonWriter = new BReferenceJsonWriter(style);
        this.valueJsonWriter = new BValueJsonWriter(style);
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