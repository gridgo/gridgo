package io.gridgo.utils.pojo.asm.getter;

import lombok.Getter;

public class DefaultGetterMethodBuilder extends AbstractGetterMethodBuilder {

    @Getter
    private static final DefaultGetterMethodBuilder instance = new DefaultGetterMethodBuilder();

    private DefaultGetterMethodBuilder() {
        // do nothing
    }
}
