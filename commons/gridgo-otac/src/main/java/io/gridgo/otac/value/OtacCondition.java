package io.gridgo.otac.value;

import java.util.HashSet;
import java.util.Set;

import io.gridgo.otac.OtacOperator;
import io.gridgo.otac.exception.OtacException;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacCondition extends OtacValue {

    public static OtacCondition condition(OtacValue left, OtacOperator operator, OtacValue right) {
        return OtacCondition.builder() //
                .left(left) //
                .right(right) //
                .operator(operator) //
                .build();
    }

    public static OtacCondition condition(OtacValue left, String operator, OtacValue right) {
        return OtacCondition.builder() //
                .left(left) //
                .right(right) //
                .operator(OtacOperator.fromString(operator)) //
                .build();
    }

    private OtacValue left;
    private OtacValue right;
    private @NonNull OtacOperator operator;

    @Override
    public Set<Class<?>> requiredImports() {
        var imports = new HashSet<Class<?>>();
        if (operator.isHasLeft()) {
            if (left == null)
                throw new OtacException("Operator `" + operator + "` require left clause");
            imports.addAll(left.requiredImports());
        }

        if (operator.isHasRight()) {
            if (right == null)
                throw new OtacException("Operator `" + operator + "` require right clause");
            imports.addAll(right.requiredImports());
        }
        return imports;
    }

    @Override
    public String toString() {
        if (operator.isHasLeft() && left == null)
            throw new OtacException("Operator `" + operator + "` require left clause");

        if (operator.isHasRight() && right == null)
            throw new OtacException("Operator `" + operator + "` require right clause");

        var sb = new StringBuilder();
        if (operator.isHasLeft())
            sb.append(left).append(" ");
        sb.append(operator.getOps());
        if (operator.isHasRight())
            sb.append(" ").append(right);
        return sb.toString();
    }
}
