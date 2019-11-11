package io.gridgo.bean.serialization.json;

import io.gridgo.bean.serialization.BSerializationPlugin;

@BSerializationPlugin(NormalCompactJsonSerializer.NAME)
public class NormalCompactJsonSerializer extends DefaultJsonSerializer {

    public static final String NAME = "jsonNormal";

    public NormalCompactJsonSerializer() {
        super(JsonCompactMode.NORMAL_COMPACT);
    }
}
