package io.gridgo.bean.serialization.json;

import static lombok.AccessLevel.PROTECTED;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import io.gridgo.bean.BElement;
import io.gridgo.bean.serialization.AbstractBSerializer;
import io.gridgo.bean.serialization.json.writer.CompositeJsonWriter;
import io.gridgo.bean.serialization.json.writer.ElementJsonWriter;
import lombok.Getter;
import lombok.NonNull;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

public abstract class AbstractJsonSerialzier extends AbstractBSerializer {

    @Getter(PROTECTED)
    private final ElementJsonWriter<BElement> jsonWriter;

    private final ThreadLocal<JSONParser> jsonParsers;

    protected AbstractJsonSerialzier(@NonNull JsonCompactMode compactMode, JsonParsingMode parsingMode) {
        switch (compactMode) {
        case LT_COMPACT:
            jsonWriter = CompositeJsonWriter.getLtCompactInstance();
            break;
        case MAX_COMPACT:
            jsonWriter = CompositeJsonWriter.getMaxCompactInstance();
            break;
        case NO_COMPACT:
            jsonWriter = CompositeJsonWriter.getNoCompactInstance();
            break;
        case NORMAL_COMPACT:
            jsonWriter = CompositeJsonWriter.getNormalCompactInstance();
            break;
        default:
            throw new IllegalArgumentException("JsonCompressMode unsupported");
        }

        if (parsingMode == null)
            parsingMode = JsonParsingMode.DEFAULT;

        switch (parsingMode) {
        case SIMPLE:
            jsonParsers = ThreadLocal.withInitial(() -> new JSONParser(JSONParser.MODE_JSON_SIMPLE));
            break;
        case PERMISSIVE:
            jsonParsers = ThreadLocal.withInitial(() -> new JSONParser(JSONParser.MODE_PERMISSIVE));
            break;
        case STRICTEST:
            jsonParsers = ThreadLocal.withInitial(() -> new JSONParser(JSONParser.MODE_STRICTEST));
            break;
        case RFC4627:
            jsonParsers = ThreadLocal.withInitial(() -> new JSONParser(JSONParser.MODE_RFC4627));
            break;
        default:
            jsonParsers = ThreadLocal.withInitial(() -> new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE));
            break;
        }
    }

    protected Object deserializeToJsonElement(InputStream in) throws ParseException, UnsupportedEncodingException {
        return jsonParsers.get().parse(in);
    }
}
