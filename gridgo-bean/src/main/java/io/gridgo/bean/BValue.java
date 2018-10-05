package io.gridgo.bean;

import java.util.Base64;

import io.gridgo.bean.exceptions.InvalidTypeException;
import io.gridgo.bean.impl.BFactory;
import io.gridgo.utils.ByteArrayUtils;
import io.gridgo.utils.PrimitiveUtils;
import io.gridgo.utils.StringUtils;

public interface BValue extends BElement {

	static BValue newDefault() {
		return BFactory.DEFAULT.newValue();
	}

	@Override
	default BType getType() {
		if (!this.isNull()) {
			if (this.getData() instanceof Boolean) {
				return BType.BOOLEAN;
			} else if (this.getData() instanceof Character) {
				return BType.CHAR;
			} else if (this.getData() instanceof Byte) {
				return BType.BYTE;
			} else if (this.getData() instanceof Short) {
				return BType.SHORT;
			} else if (this.getData() instanceof Integer) {
				return BType.INTEGER;
			} else if (this.getData() instanceof Float) {
				return BType.FLOAT;
			} else if (this.getData() instanceof Long) {
				return BType.LONG;
			} else if (this.getData() instanceof Double) {
				return BType.DOUBLE;
			} else if (this.getData() instanceof String) {
				return BType.STRING;
			} else if (this.getData() instanceof byte[]) {
				return BType.RAW;
			}
			throw new InvalidTypeException("Cannot recognize data type: " + this.getData().getClass());
		}
		return BType.NULL;
	}

	void setData(Object data);

	Object getData();

	default boolean isNull() {
		return this.getData() == null;
	}

	default boolean getBoolean() {
		if (!this.isNull()) {
			return PrimitiveUtils.getBooleanValueFrom(this.getData());
		}
		throw new NullPointerException("BValue contains null data");
	}

	default char getChar() {
		if (!this.isNull()) {
			return PrimitiveUtils.getCharValueFrom(this.getData());
		}
		throw new NullPointerException("BValue contains null data");
	}

	default byte getByte() {
		if (!this.isNull()) {
			return PrimitiveUtils.getByteValueFrom(this.getData());
		}
		throw new NullPointerException("BValue contains null data");
	}

	default short getShort() {
		if (!this.isNull()) {
			return PrimitiveUtils.getShortValueFrom(this.getData());
		}
		throw new NullPointerException("BValue contains null data");
	}

	default int getInteger() {
		if (!this.isNull()) {
			return PrimitiveUtils.getIntegerValueFrom(this.getData());
		}
		throw new NullPointerException("BValue contains null data");
	}

	default float getFloat() {
		if (!this.isNull()) {
			return PrimitiveUtils.getFloatValueFrom(this.getData());
		}
		throw new NullPointerException("BValue contains null data");
	}

	default long getLong() {
		if (!this.isNull()) {
			return PrimitiveUtils.getLongValueFrom(this.getData());
		}
		throw new NullPointerException("BValue contains null data");
	}

	default double getDouble() {
		if (!this.isNull()) {
			return PrimitiveUtils.getDoubleValueFrom(this.getData());
		}
		throw new NullPointerException("BValue contains null data");
	}

	default String getString() {
		if (!this.isNull()) {
			return PrimitiveUtils.getStringValueFrom(this.getData());
		}
		return null;
	}

	default byte[] getRaw() {
		if (!this.isNull()) {
			if (this.getData() instanceof byte[]) {
				return (byte[]) this.getData();
			} else if (this.getData() instanceof String) {
				return ((String) this.getData()).getBytes();
			} else {
				throw new InvalidTypeException(
						"BValue contains data which cannot convert to byte[]: " + this.getType());
			}
		}
		return null;
	}

	@Override
	default String toJson() {
		if (!this.isNull()) {
			return this.getString();
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	default Object toJsonElement() {
		if (!this.isNull()) {
			if (this.getData() instanceof byte[]) {
				return ByteArrayUtils.toHex(this.getRaw(), "0x");
			} else if (this.getData() instanceof Character) {
				return new String(new char[] { this.getChar() });
			}
			return this.getData();
		}
		return null;
	}

	@Override
	default String toXml(String name) {
		if (!this.isNull()) {
			String type = this.getType().name().toLowerCase();
			StringBuilder sb = new StringBuilder();
			sb.append("<").append(type);
			if (name != null) {
				sb.append(" name=\"").append(name).append("\"");
			}
			String content = this.getString();
			if (content.contains("<")) {
				sb.append(">") //
						.append("<![CDATA[")//
						.append(content) //
						.append("]]>") //
						.append("</").append(type).append(">");
			} else if (content.contains("\"")) {
				sb.append(">") //
						.append(content) //
						.append("</").append(type).append(">");
			} else {
				sb.append(" value=\"").append(content.replaceAll("\"", "\\\"")).append("\"/>");
			}
			return sb.toString();
		}
		return name == null ? "<null />" : ("<null name=\"" + name + "\"/>");
	}

	default void encodeHex() {
		if (!(this.getData() instanceof byte[])) {
			throw new InvalidTypeException("Cannot encode hex from data which is not in raw (byte[]) format");
		}
		this.setData(ByteArrayUtils.toHex(getRaw(), "0x"));
	}

	default void decodeHex() {
		if (!(this.getData() instanceof String)) {
			throw new InvalidTypeException("Cannot decode hex from data which is not in String format");
		}
		String hex = this.getString();
		this.setData(ByteArrayUtils.fromHex(hex));
	}

	default void encodeBase64() {
		if (!(this.getData() instanceof byte[])) {
			throw new InvalidTypeException("Cannot encode base64 from data which is not in raw (byte[]) format");
		}
		this.setData(Base64.getEncoder().encodeToString(getRaw()));
	}

	default void decodeBase64() {
		if (!(this.getData() instanceof String)) {
			throw new InvalidTypeException("Cannot decode base64 from data which is not in String format");
		}
		String base64 = this.getString();
		this.setData(Base64.getDecoder().decode(base64));
	}

	default void convertToBoolean() {
		this.setData(this.getBoolean());
	}

	default void convertToChar() {
		this.setData(this.getChar());
	}

	default void convertToByte() {
		this.setData(this.getByte());
	}

	default void convertToShort() {
		this.setData(this.getShort());
	}

	default void convertToInteger() {
		this.setData(this.getInteger());
	}

	default void convertToLong() {
		this.setData(this.getLong());
	}

	default void convertToFloat() {
		this.setData(this.getFloat());
	}

	default void convertToDouble() {
		this.setData(this.getDouble());
	}

	default void convertToRaw() {
		this.setData(this.getRaw());
	}

	default void convertToString() {
		this.setData(this.getString());
	}

	@Override
	default void writeString(String name, int numTab, StringBuilder writer) {
		StringUtils.tabs(numTab, writer);
		BType type = this.getType();
		String content = this.getString();
		if (name == null) {
			writer.append("(").append(type.name()).append(")");
		} else {
			writer.append(name).append(": ").append(type.name());
		}
		if (!this.isNull()) {
			writer.append(" = ").append(content);
		}
	}
}
