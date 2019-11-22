package io.gridgo.bean.impl;

import java.util.Arrays;

import io.gridgo.bean.BType;
import io.gridgo.bean.BValue;
import io.gridgo.bean.exceptions.InvalidTypeException;
import io.gridgo.utils.PrimitiveUtils;
import io.gridgo.utils.annotations.Transient;
import io.gridgo.utils.hash.BinaryHashCodeCalculator;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MutableBValue extends AbstractBElement implements BValue {

    private transient static final BinaryHashCodeCalculator binaryHashCodeCalculator = BinaryHashCodeCalculator.XXHASH32_JNI;

    @Getter
    private Object data;

    @Getter
    @Transient
    private transient BType type = BType.NULL;

    @Transient
    private transient int hashCode;

    @Transient
    private volatile transient boolean hashCodeFlag = true;

    public MutableBValue(Object data) {
        if (data != null && !(data instanceof byte[]) && !PrimitiveUtils.isPrimitive(data.getClass())) {
            throw new InvalidTypeException("Cannot create DefaultBValue from: " + data.getClass() + " instance");
        }
        this.setData(data);
    }

    @Override
    public void setData(Object data) {
        this.data = data;
        this.type = BValue.super.getType();
        this.hashCodeFlag = true;
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

    /**
     * Optimized hash code calculating for binary
     */
    @Override
    public int hashCode() {
        if (hashCodeFlag) {
            if (data == null)
                this.hashCode = super.hashCode();
            else if (data instanceof byte[])
                this.hashCode = binaryHashCodeCalculator.calcHashCode((byte[]) data);
            else
                this.hashCode = data.hashCode();

            hashCodeFlag = false;
        }

        return this.hashCode;
    }
}