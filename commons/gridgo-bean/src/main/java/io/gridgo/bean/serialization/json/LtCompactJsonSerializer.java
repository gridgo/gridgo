package io.gridgo.bean.serialization.json;

import static io.gridgo.bean.serialization.json.JsonCompactMode.LT_COMPACT;

import io.gridgo.bean.serialization.BSerializationPlugin;

@BSerializationPlugin(LtCompactJsonSerializer.NAME)
public class LtCompactJsonSerializer extends DefaultJsonSerializer {

    public static final String NAME = "jsonLtCompact";

    public LtCompactJsonSerializer() {
        super(LT_COMPACT);
    }
}
