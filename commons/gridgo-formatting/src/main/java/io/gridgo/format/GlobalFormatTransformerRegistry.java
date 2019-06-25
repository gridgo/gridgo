package io.gridgo.format;

import lombok.Getter;

public class GlobalFormatTransformerRegistry extends DefaultFormatTransformerRegistry {

    @Getter
    private final static GlobalFormatTransformerRegistry instance = new GlobalFormatTransformerRegistry();

    private GlobalFormatTransformerRegistry() {
        this.inherit(FormatTransformerRegistry.newwDefault());
    }
}
