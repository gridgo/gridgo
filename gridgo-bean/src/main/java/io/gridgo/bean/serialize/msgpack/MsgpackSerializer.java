package io.gridgo.bean.serialize.msgpack;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map.Entry;

import org.msgpack.core.MessageFormat;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BType;
import io.gridgo.bean.BValue;
import io.gridgo.bean.exceptions.InvalidTypeException;
import io.gridgo.bean.impl.BFactory;
import io.gridgo.bean.impl.BFactoryAware;
import io.gridgo.bean.serialize.BSerializer;
import lombok.Setter;

public class MsgpackSerializer implements BSerializer, BFactoryAware {

	@Setter
	private BFactory factory;

	private void packAny(BElement element, MessagePacker packer) throws IOException {
		if (element instanceof BValue) {
			this.packValue(element.asValue(), packer);
		} else if (element instanceof BArray) {
			this.packArray(element.asArray(), packer);
		} else if (element instanceof BObject) {
			this.packObject(element.asObject(), packer);
		} else {
			throw new IllegalArgumentException("Unrecoginzed BElement implementation: " + element.getClass());
		}
	}

	private void packValue(BValue value, MessagePacker packer) throws IOException {
		BType type = value.getType();
		if (type != null) {
			switch (type) {
			case BOOLEAN:
				packer.packBoolean(value.getBoolean());
				return;
			case BYTE:
				packer.packByte(value.getByte());
				return;
			case CHAR:
				packer.packShort(value.getShort());
				return;
			case DOUBLE:
				packer.packDouble(value.getDouble());
				return;
			case FLOAT:
				packer.packFloat(value.getFloat());
				return;
			case INTEGER:
				packer.packInt(value.getInteger());
				return;
			case LONG:
				packer.packLong(value.getLong());
				return;
			case NULL:
				packer.packNil();
				return;
			case RAW:
				byte[] bytes = value.getRaw();
				packer.packBinaryHeader(bytes.length);
				packer.addPayload(bytes);
				return;
			case SHORT:
				packer.packShort(value.getShort());
				return;
			case STRING:
				packer.packString(value.getString());
				return;
			default:
				break;
			}
		}
		throw new InvalidTypeException("Cannot writeValue object type: " + type);
	}

	private void packObject(BObject value, MessagePacker packer) throws IOException {
		packer.packMapHeader(value.size());
		for (Entry<String, BElement> entry : value.entrySet()) {
			packer.packString(entry.getKey());
			packAny(entry.getValue(), packer);
		}
	}

	private void packArray(BArray value, MessagePacker packer) throws IOException {
		packer.packArrayHeader(value.size());
		for (BElement entry : value) {
			packAny(entry, packer);
		}
	}

	@Override
	public void serialize(BElement element, OutputStream out) {
		if (element == null) {
			throw new NullPointerException("Cannot serialize null element");
		} else if (out == null) {
			throw new NullPointerException("Cannot serialize with null output stream");
		}

		try (MessagePacker packer = MessagePack.newDefaultPacker(out)) {
			packAny(element, packer);
			packer.flush();
		} catch (IOException e) {
			throw new RuntimeException("Error while serialize element", e);
		}
	}

	private BArray unpackArray(MessageUnpacker unpacker) throws IOException {
		BArray result = this.getFactory().newArray();
		int size = unpacker.unpackArrayHeader();
		for (int i = 0; i < size; i++) {
			result.addAny(this.unpackAny(unpacker));
		}
		return result;
	}

	private BObject unpackMap(MessageUnpacker unpacker) throws IOException {
		BObject result = this.getFactory().newObject();
		int size = unpacker.unpackMapHeader();
		for (int i = 0; i < size; i++) {
			result.putAny(unpacker.unpackString(), unpackAny(unpacker));
		}
		return result;
	}

	private BValue unpackValue(MessageFormat format, MessageUnpacker unpacker) throws IOException {
		BValue value = this.getFactory().newValue();
		switch (format.getValueType()) {
		case BINARY:
			int len = unpacker.unpackBinaryHeader();
			value.setData(unpacker.readPayload(len));
			break;
		case BOOLEAN:
			value.setData(unpacker.unpackBoolean());
			break;
		case FLOAT:
			if (format == MessageFormat.FLOAT64) {
				value.setData(unpacker.unpackDouble());
			} else {
				value.setData(unpacker.unpackFloat());
			}
			break;
		case INTEGER:
			if (format == MessageFormat.INT8) {
				value.setData(unpacker.unpackByte());
			} else if (format == MessageFormat.INT16 || format == MessageFormat.UINT8) {
				value.setData(unpacker.unpackShort());
			} else if (format == MessageFormat.UINT32 || format == MessageFormat.INT64
					|| format == MessageFormat.UINT64) {
				value.setData(unpacker.unpackLong());
			} else {
				value.setData(unpacker.unpackInt());
			}
			break;
		case NIL:
			unpacker.unpackNil();
			break;
		case STRING:
			value.setData(unpacker.unpackString());
			break;
		default:
			throw new InvalidTypeException("Cannot unpack value from data format: " + format);
		}
		return value;
	}

	private BElement unpackAny(MessageUnpacker unpacker) throws IOException {
		MessageFormat format = unpacker.getNextFormat();
		switch (format.getValueType()) {
		case ARRAY:
			return unpackArray(unpacker);
		case MAP:
			return unpackMap(unpacker);
		case BINARY:
		case BOOLEAN:
		case FLOAT:
		case INTEGER:
		case NIL:
		case STRING:
			return unpackValue(format, unpacker);
		case EXTENSION:
		default:
			break;
		}
		throw new InvalidTypeException("Cannot deserialize as BElement for format: " + format);
	}

	@Override
	public BElement deserialize(InputStream in) {
		try (MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(in)) {
			return this.unpackAny(unpacker);
		} catch (IOException e) {
			throw new RuntimeException("Error while deserialize input stream", e);
		}
	}
}
