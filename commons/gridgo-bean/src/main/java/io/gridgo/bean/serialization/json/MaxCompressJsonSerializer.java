package io.gridgo.bean.serialization.json;

import static io.gridgo.bean.serialization.json.JsonCompressMode.MAX_COMPRESS;

import io.gridgo.bean.serialization.BSerializationPlugin;

@BSerializationPlugin(MaxCompressJsonSerializer.NAME)
public class MaxCompressJsonSerializer extends DefaultJsonSerializer {

    public static final String NAME = "jsonMaxCompress";

    public MaxCompressJsonSerializer() {
        super(MAX_COMPRESS);
    }
}
