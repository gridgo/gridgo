package io.gridgo.utils.pojo.asm.setter;

import lombok.Getter;

public class DefaultSetterMethodBuilder extends AbstractSetterMethodBuilder {

    @Getter
    private final static DefaultSetterMethodBuilder instance = new DefaultSetterMethodBuilder();

    private DefaultSetterMethodBuilder() {
        // do nothing
    }
}
