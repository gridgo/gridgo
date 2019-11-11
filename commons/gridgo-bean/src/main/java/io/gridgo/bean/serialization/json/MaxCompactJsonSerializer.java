package io.gridgo.bean.serialization.json;

import static io.gridgo.bean.serialization.json.JsonCompactMode.MAX_COMPACT;

import io.gridgo.bean.serialization.BSerializationPlugin;

@BSerializationPlugin(MaxCompactJsonSerializer.NAME)
public class MaxCompactJsonSerializer extends DefaultJsonSerializer {

    public static final String NAME = "jsonMaxCompress";

    public MaxCompactJsonSerializer() {
        super(MAX_COMPACT);
    }
}
