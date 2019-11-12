package io.gridgo.bean;

import java.util.Base64;

import io.gridgo.bean.exceptions.InvalidTypeException;
import io.gridgo.bean.factory.BFactory;
import io.gridgo.utils.ByteArrayUtils;
import io.gridgo.utils.PrimitiveUtils;

public interface BValue extends BElement {

    static BValue ofEmpty() {
        return BFactory.newDefaultValue();
    }

    static BValue of(Object data) {
        return BFactory.DEFAULT.newValue(data);
    }

    @Override
    default boolean isContainer() {
        return false;
    }

    @Override
    default boolean isArray() {
        return false;
    }

    @Override
    default boolean isValue() {
        return true;
    }

    @Override
    default boolean isObject() {
        return false;
    }

    @Override
    default boolean isReference() {
        return false;
    }

    @Override
    default BType getType() {
        if (this.isNull())
            return BType.NULL;
        if (Boolean.class.isInstance(this.getData()))
            return BType.BOOLEAN;
        if (Character.class.isInstance(this.getData()))
            return BType.CHAR;
        if (Byte.class.isInstance(this.getData()))
            return BType.BYTE;
        if (Short.class.isInstance(this.getData()))
            return BType.SHORT;
        if (Integer.class.isInstance(this.getData()))
            return BType.INTEGER;
        if (Float.class.isInstance(this.getData()))
            return BType.FLOAT;
        if (Long.class.isInstance(this.getData()))
            return BType.LONG;
        if (Double.class.isInstance(this.getData()))
            return BType.DOUBLE;
        if (String.class.isInstance(this.getData()))
            return BType.STRING;
        if (byte[].class.isInstance(this.getData()))
            return BType.RAW;
        if (Number.class.isInstance(this.getData()))
            return BType.GENERIC_NUMBER;

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

    @SuppressWarnings("unchecked")
    @Override
    default <T> T getInnerValue() {
        return (T) getData();
    }
}
