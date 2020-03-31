package io.gridgo.pojo.otac;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum OtacInheritOperator {

    NONE(""), SUPER("super "), EXTENDS("extends "), IMPLEMENTS("implements ");

    @Getter
    private final String keywork;
}
