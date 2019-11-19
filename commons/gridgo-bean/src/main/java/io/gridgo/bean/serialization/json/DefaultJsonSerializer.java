package io.gridgo.bean.serialization.json;

import io.gridgo.bean.serialization.BSerializationPlugin;

@BSerializationPlugin(DefaultJsonSerializer.NAME)
public class DefaultJsonSerializer extends AbstractJsonSerializier {

    public static final String NAME = "json";

    protected DefaultJsonSerializer(JsonCompactMode compactMode) {
        super(compactMode);
    }

    public DefaultJsonSerializer() {
        this(JsonCompactMode.FULL);
    }
}
