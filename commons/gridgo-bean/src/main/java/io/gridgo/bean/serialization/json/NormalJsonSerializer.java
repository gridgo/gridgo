package io.gridgo.bean.serialization.json;

import io.gridgo.bean.serialization.BSerializationPlugin;

@BSerializationPlugin(NormalJsonSerializer.NAME)
public class NormalJsonSerializer extends DefaultJsonSerializer {

    public static final String NAME = "jsonNormal";

    public NormalJsonSerializer() {
        super(JsonCompressMode.NORMAL_COMPRESS);
    }
}
