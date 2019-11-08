package io.gridgo.bean.serialization.json;

import static io.gridgo.bean.serialization.json.JsonCompressMode.LT_COMPRESS;

import io.gridgo.bean.serialization.BSerializationPlugin;

@BSerializationPlugin(LtCompressJsonSerializer.NAME)
public class LtCompressJsonSerializer extends DefaultJsonSerializer {

    public static final String NAME = "jsonLtCompress";

    public LtCompressJsonSerializer() {
        super(LT_COMPRESS);
    }
}
