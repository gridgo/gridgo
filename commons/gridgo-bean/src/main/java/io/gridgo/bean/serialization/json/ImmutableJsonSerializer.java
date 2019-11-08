package io.gridgo.bean.serialization.json;

import io.gridgo.bean.serialization.BSerializationPlugin;

@BSerializationPlugin(ImmutableJsonSerializer.NAME)
public class ImmutableJsonSerializer extends DefaultJsonSerializer {

    public static final String NAME = "jsonro";

    public ImmutableJsonSerializer() {
        super(false);
    }
}
