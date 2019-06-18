package io.gridgo.bean;

import java.util.Base64;

import io.gridgo.bean.exceptions.InvalidTypeException;
import io.gridgo.bean.factory.BFactory;
import io.gridgo.utils.ByteArrayUtils;
import io.gridgo.utils.PrimitiveUtils;

public interface BValue extends BElement {

    static BValue ofEmpty() {
        return BFactory.DEFAULT.newValue();
    }

    static BValue of(Object data) {
        return BFactory.DEFAULT.newValue(data);
    }

    @Override
    default BType getType() {
        if (this.isNull()) {
            return BType.NULL;
        }

        if (this.getData() instanceof Boolean) {
            return BType.BOOLEAN;
        }
        if (this.getData() instanceof Character) {
            return BType.CHAR;
        }
        if (this.getData() instanceof Byte) {
            return BType.BYTE;
        }
        if (this.getData() instanceof Short) {
            return BType.SHORT;
        }
        if (this.getData() instanceof Integer) {
            return BType.INTEGER;
        }
        if (this.getData() instanceof Float) {
            return BType.FLOAT;
        }
        if (this.getData() instanceof Long) {
            return BType.LONG;
        }
        if (this.getData() instanceof Double) {
            return BType.DOUBLE;
        }
        if (this.getData() instanceof String) {
            return BType.STRING;
        }
        if (this.getData() instanceof byte[]) {
            return BType.RAW;
        }
        if (this.getData() instanceof Number) {
            return BType.GENERIC_NUMBER;
        }
        throw new InvalidTypeException("Cannot recognize data type: " + this.getData().getClass());
    }

    void setData(Object data);

    Object getData();

    default boolean isNull() {
        return this.getData() == null;
    }

    default Boolean getBoolean() {
        if (!this.isNull()) {
            return PrimitiveUtils.getBooleanValueFrom(this.getData());
        }
        return null;
    }

    default Character getChar() {
        if (!this.isNull()) {
            return PrimitiveUtils.getCharValueFrom(this.getData());
        }
        return null;
    }

    default Byte getByte() {
        if (!this.isNull()) {
            return PrimitiveUtils.getByteValueFrom(this.getData());
        }
        return null;
    }

    default Short getShort() {
        if (!this.isNull()) {
            return PrimitiveUtils.getShortValueFrom(this.getData());
        }
        return null;
    }

    default Integer getInteger() {
        if (!this.isNull()) {
            return PrimitiveUtils.getIntegerValueFrom(this.getData());
        }
        return null;
    }

    default Float getFloat() {
        if (!this.isNull()) {
            return PrimitiveUtils.getFloatValueFrom(this.getData());
        }
        return null;
    }

    default Long getLong() {
        if (!this.isNull()) {
            return PrimitiveUtils.getLongValueFrom(this.getData());
        }
        return null;
    }

    default Double getDouble() {
        if (!this.isNull()) {
            return PrimitiveUtils.getDoubleValueFrom(this.getData());
        }
        return null;
    }

    default String getString() {
        if (!this.isNull()) {
            return PrimitiveUtils.getStringValueFrom(this.getData());
        }
        return null;
    }

    default byte[] getRaw() {
        if (!this.isNull()) {
            return ByteArrayUtils.primitiveToBytes(this.getData());
        }
        return null;
    }

    default BValue encodeHex() {
        if (!(this.getData() instanceof byte[])) {
            throw new InvalidTypeException("Cannot encode hex from data which is not in raw (byte[]) format");
        }
        this.setData(ByteArrayUtils.toHex(getRaw(), "0x"));
        return this;
    }

    default BValue decodeHex() {
        if (this.getData() instanceof byte[]) {
            // skip decode if data already in byte[]
            return this;
        }
        if (!(this.getData() instanceof String)) {
            throw new InvalidTypeException("Cannot decode hex from data which is not in String format");
        }
        String hex = this.getString();
        this.setData(ByteArrayUtils.fromHex(hex));
        return this;
    }

    default BValue encodeBase64() {
        if (!(this.getData() instanceof byte[])) {
            throw new InvalidTypeException("Cannot encode base64 from data which is not in raw (byte[]) format");
        }
        this.setData(Base64.getEncoder().encodeToString(getRaw()));
        return this;
    }

    default BValue decodeBase64() {
        if (!(this.getData() instanceof String)) {
            throw new InvalidTypeException("Cannot decode base64 from data which is not in String format");
        }
        String base64 = this.getString();
        this.setData(Base64.getDecoder().decode(base64));
        return this;
    }

    default <T> T getDataAs(Class<T> targetType) {
        return PrimitiveUtils.getValueFrom(targetType, this.getData());
    }

    default BValue convertToBoolean() {
        this.setData(this.getBoolean());
        return this;
    }

    default BValue convertToChar() {
        this.setData(this.getChar());
        return this;
    }

    default BValue convertToByte() {
        this.setData(this.getByte());
        return this;
    }

    default BValue convertToShort() {
        this.setData(this.getShort());
        return this;
    }

    default BValue convertToInteger() {
        this.setData(this.getInteger());
        return this;
    }

    default BValue convertToLong() {
        this.setData(this.getLong());
        return this;
    }

    default BValue convertToFloat() {
        this.setData(this.getFloat());
        return this;
    }

    default BValue convertToDouble() {
        this.setData(this.getDouble());
        return this;
    }

    default BValue convertToRaw() {
        this.setData(this.getRaw());
        return this;
    }

    default BValue convertToString() {
        this.setData(this.getString());
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    default <T extends BElement> T deepClone() {
        return (T) of(this.getData());
    }

    @Override
    @SuppressWarnings("unchecked")
    default <T> T toJsonElement() {
        switch (this.getType()) {
        case RAW:
            return (T) ByteArrayUtils.toHex(this.getRaw(), "0x");
        case CHAR:
            return (T) this.getString();
        default:
            return (T) this.getData();
        }
    }
}
