package io.gridgo.otac;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum OtacAccessLevel {

    PUBLIC("public "), PROTECTED("protected "), PRIVATE("private "), PACKAGE("");

    @Getter
    private final String keyword;
}
