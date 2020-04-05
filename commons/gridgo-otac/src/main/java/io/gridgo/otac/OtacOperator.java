package io.gridgo.otac;

import lombok.Getter;

@Getter
public enum OtacOperator {

    // LOGIC
    AND("&&"),
    OR("||"),
    XOR("^"),
    NOT("!", false, true),
    // COMPARE
    EQUALS("=="),
    NOT_EQUALS("!="),
    GREATER_THAN(">"),
    GREATER_OR_EQUALS(">="),
    LESS_THAN("<"),
    LESS_OR_EQUALS("<="),
    // MATH
    ADD("+"), //
    SUBTRACT("-"),
    MULTIPLY("*"),
    DIVIDE("/"), //
    // BITWISE
    AND_BIT("&"), //
    OR_BIT("|");

    private final String ops;
    private final boolean hasLeft;
    private final boolean hasRight;

    private OtacOperator(String ops, boolean hasLeft, boolean hasRight) {
        this.ops = ops;
        this.hasLeft = hasLeft;
        this.hasRight = hasRight;
    }

    private OtacOperator(String ops) {
        this(ops, true, true);
    }

    public static OtacOperator fromString(String ops) {
        if (ops == null || ops.isBlank())
            return null;
        for (var value : values())
            if (value.getOps().equals(ops))
                return value;
        return null;
    }
}
