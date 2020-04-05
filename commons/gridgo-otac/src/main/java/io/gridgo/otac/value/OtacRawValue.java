package io.gridgo.otac.value;

import static io.gridgo.otac.OtacType.typeOf;

import io.gridgo.otac.OtacType;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OtacRawValue extends OtacValue {
    private Object value;

    public static OtacRawValue of(Object value) {
        return OtacRawValue.builder().value(value).build();
    }

    @Override
    public OtacType inferredType() {
        if (value == null)
            return super.inferredType();
        return typeOf(value.getClass());
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        if (value == null) {
            sb.append("null");
        } else {
            if (value.getClass() == String.class)
                sb.append('"');
            sb.append(value);
            if (value.getClass() == String.class)
                sb.append('"');
        }
        return sb.toString();
    }
}
