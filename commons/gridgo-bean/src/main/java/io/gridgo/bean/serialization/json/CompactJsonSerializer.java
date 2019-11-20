package io.gridgo.bean.serialization.json;

import io.gridgo.bean.serialization.BSerializationPlugin;

@BSerializationPlugin(CompactJsonSerializer.NAME)
public class CompactJsonSerializer extends AbstractJsonSerializer {

    public static final String NAME = "jsonCompact";

    public CompactJsonSerializer() {
        super(JsonCompactMode.COMPACT);
    }
}
