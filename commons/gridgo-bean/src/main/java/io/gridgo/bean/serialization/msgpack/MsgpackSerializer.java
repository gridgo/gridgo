package io.gridgo.bean.serialization.msgpack;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;

import org.msgpack.core.MessageFormat;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;

import io.gridgo.bean.BArray;
import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.bean.BValue;
import io.gridgo.bean.exceptions.BeanSerializationException;
import io.gridgo.bean.exceptions.InvalidTypeException;
import io.gridgo.bean.serialization.AbstractBSerializer;
import io.gridgo.bean.serialization.BSerializationPlugin;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@BSerializationPlugin({ MsgpackSerializer.NAME, "raw" })
public class MsgpackSerializer extends AbstractBSerializer {

    public static final String NAME = "msgpack";

    private void packAny(BElement element, MessagePacker packer) throws IOException {
        if (element instanceof BValue) {
            this.packValue(element.asValue(), packer);
        } else if (element instanceof BArray) {
            this.packArray(element.asArray(), packer);
        } else if (element instanceof BObject) {
            this.packObject(element.asObject(), packer);
        } else {
            if (log.isWarnEnabled())
                log.warn("Unrecoginzed BElement implementation: {}", element.getClass());
        }
    }

    private void packValue(BValue value, MessagePacker packer) throws IOException {
        var type = value.getType();
        if (type != null) {
            switch (type) {
            case BOOLEAN:
                packer.packBoolean(value.getBoolean());
                return;
            case BYTE:
                packer.packByte(value.getByte());
                return;
            case CHAR:
            case SHORT:
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
            case STRING:
                packer.packString(value.getString());
                return;
            default:
                break;
            }
        }
        throw new InvalidTypeException("Cannot writeValue object type: " + type);
    }

    private void packObject(BObject object, MessagePacker packer) throws IOException {
        var tobePacked = new HashMap<String, BElement>();
        for (var entry : object.entrySet()) {
            if (entry.getValue().isValue() || entry.getValue().isArray() || entry.getValue().isObject()) {
                tobePacked.put(entry.getKey(), entry.getValue());
            } else if (log.isWarnEnabled()) {
                log.warn("Ignore key {} while packing bObject because of value cannot be packed in msgpack format", entry.getKey());
            }
        }
        packer.packMapHeader(tobePacked.size());
        for (var entry : tobePacked.entrySet()) {
            packer.packString(entry.getKey());
            packAny(entry.getValue(), packer);
        }
    }

    private void packArray(BArray array, MessagePacker packer) throws IOException {
        var tobePacked = new LinkedList<BElement>();
        for (var entry : array) {
            if (entry.isValue() || entry.isArray() || entry.isObject()) {
                tobePacked.add(entry);
            }
        }
        packer.packArrayHeader(tobePacked.size());
        for (var entry : tobePacked) {
            packAny(entry, packer);
        }
    }

    @Override
    public void serialize(@NonNull BElement element, @NonNull OutputStream out) {
        try (var packer = MessagePack.newDefaultPacker(out)) {
            packAny(element, packer);
            packer.flush();
        } catch (IOException e) {
            throw new BeanSerializationException("Error while serialize element", e);
        }
    }

    private BArray unpackArray(MessageUnpacker unpacker) throws IOException {
        var result = this.getFactory().newArray();
        int size = unpacker.unpackArrayHeader();
        for (int i = 0; i < size; i++) {
            result.addAny(this.unpackAny(unpacker));
        }
        return result;
    }

    private BObject unpackMap(MessageUnpacker unpacker) throws IOException {
        var result = this.getFactory().newObject();
        int size = unpacker.unpackMapHeader();
        for (int i = 0; i < size; i++) {
            var key = unpacker.unpackString();
            var value = unpackAny(unpacker);
            result.putAny(key, value);
        }
        return result;
    }

    private BValue unpackValue(MessageFormat format, MessageUnpacker unpacker) throws IOException {
        var value = this.getFactory().newValue();
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
            } else if (format == MessageFormat.UINT32 || format == MessageFormat.INT64 || format == MessageFormat.UINT64) {
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
        var format = unpacker.getNextFormat();
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
        try (var unpacker = MessagePack.newDefaultUnpacker(in)) {
            return this.unpackAny(unpacker);
        } catch (IOException e) {
            throw new BeanSerializationException("Error while deserialize input stream", e);
        }
    }
}
