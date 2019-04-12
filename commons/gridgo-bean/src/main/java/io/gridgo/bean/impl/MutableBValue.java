package io.gridgo.bean.impl;

import java.util.Arrays;

import io.gridgo.bean.BValue;
import io.gridgo.bean.exceptions.InvalidTypeException;
import io.gridgo.bean.serialization.text.BPrinter;
import io.gridgo.utils.PrimitiveUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class MutableBValue extends AbstractBElement implements BValue {

    @Setter
    @Getter
    private Object data;

    public MutableBValue(Object data) {
        if (data != null && !(data instanceof byte[]) && !PrimitiveUtils.isPrimitive(data.getClass())) {
            throw new InvalidTypeException("Cannot create DefaultBValue from: " + data.getClass() + " instance");
        }
        this.setData(data);
    }

    @Override
    public String toString() {
        StringBuilder writer = new StringBuilder();
        BPrinter.print(writer, this);
        return writer.toString();
    }

    @Override
    public boolean equals(Object obj) {
        var otherData = obj;
        if (obj instanceof BValue) {
            otherData = ((BValue) obj).getData();
        }

        if (data == null)
            return otherData == null;

        if (otherData == null)
            return false;

        if (data == otherData || data.equals(otherData))
            return true;

        if (data instanceof Number && otherData instanceof Number)
            return ((Number) data).doubleValue() == ((Number) otherData).doubleValue();

        if (data instanceof String && otherData instanceof Character)
            return data.equals(String.valueOf((Character) otherData));

        if (data instanceof Character && otherData instanceof String)
            return otherData.equals(String.valueOf((Character) data));

        if (data instanceof byte[] && otherData instanceof byte[])
            return Arrays.equals((byte[]) data, (byte[]) otherData);

        return false;
    }

    @Override
    public int hashCode() {
        return data != null ? data.hashCode() : super.hashCode();
    }
}