package io.gridgo.bean.serialization.msgpack;

import static io.gridgo.utils.pojo.PojoUtils.getGetterProxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.msgpack.core.MessageFormat;
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
import io.gridgo.utils.ArrayUtils;
import io.gridgo.utils.PrimitiveUtils;
import io.gridgo.utils.exception.RuntimeIOException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@BSerializationPlugin({ "raw", MsgpackSerializer.NAME })
public class MsgpackSerializer extends AbstractBSerializer {

    public static final String NAME = "msgpack";
    private final ThreadLocal<MsgpackerAndBuffer> PACKERS = ThreadLocal.withInitial(MsgpackerAndBuffer::new);
    private final ThreadLocal<MsgunpackerAndBuffer> UNPACKERS = ThreadLocal.withInitial(MsgunpackerAndBuffer::new);

    private void packAny(Object obj, MessagePacker packer) throws IOException {
        if (obj == null) {
            packer.packNil();
            return;
        }

        if (BElement.class.isInstance(obj)) {
            BElement element = (BElement) obj;

            if (element.isValue()) {
                packValue(element.asValue(), packer);
                return;
            }

            if (element.isArray()) {
                packArray(element.asArray(), packer);
                return;
            }

            if (element.isObject()) {
                packMap(element.asObject(), packer);
                return;
            }

            if (element.isReference()) {
                packAny(element.asReference().getReference(), packer);
                return;
            }

            throw new BeanSerializationException("Cannot serialize belement which instance of: " + element.getClass());
        }

        Class<?> type;
        if (Collection.class.isInstance(obj) || (type = obj.getClass()).isArray()) {
            packArray(obj, packer);
            return;
        }

        if (Map.class.isInstance(obj)) {
            packMap(obj, packer);
            return;
        }

        if (PrimitiveUtils.isPrimitive(type)) {
            packValue(obj, packer);
            return;
        }

        packPojo(obj, packer);
    }

    private void packValue(Object obj, MessagePacker packer) throws IOException {
        var value = BValue.of(obj);
        var type = value.getType();
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
            throw new BeanSerializationException("Cannot writeValue object type: " + type);
        }
    }

    private void packMap(Object obj, MessagePacker packer) throws IOException {
        if (BObject.class.isInstance(obj)) {
            BObject object = (BObject) obj;
            packer.packMapHeader(object.size());
            for (var entry : object.entrySet()) {
                packer.packString(entry.getKey());
                packAny(entry.getValue(), packer);
            }
            return;
        }

        var map = (Map<?, ?>) obj;
        packer.packMapHeader(map.size());
        for (Entry<?, ?> entry : map.entrySet()) {
            packer.packString(entry.getKey().toString());
            packAny(entry.getValue(), packer);
        }
    }

    private void packArray(Object obj, MessagePacker packer) throws IOException {
        if (BArray.class.isInstance(obj)) {
            BArray array = (BArray) obj;
            packer.packArrayHeader(array.size());
            for (var entry : array) {
                packAny(entry, packer);
            }
            return;
        }

        packer.packArrayHeader(ArrayUtils.length(obj));
        ArrayUtils.foreach(obj, ele -> {
            try {
                packAny(ele, packer);
            } catch (IOException e) {
                throw new RuntimeIOException(e);
            }
        });
    }

    private void packPojo(Object target, MessagePacker packer) throws IOException {
        var proxy = getGetterProxy(target.getClass());
        packer.packMapHeader(proxy.getFields().length);
        proxy.walkThrough(target, (signature, value) -> {
            try {
                packer.packString(signature.getTransformedOrDefaultFieldName());
                packAny(value, packer);
            } catch (IOException e) {
                throw new RuntimeIOException(e);
            }
        });
    }

    @Override
    public void serialize(@NonNull BElement element, @NonNull OutputStream out) {
        try (var holder = PACKERS.get()) {
            var packer = holder.reset(out);
            packAny(element, packer);
            packer.flush();
        } catch (Exception e) {
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
        case NIL:
            unpacker.unpackNil();
            break;
        case BINARY:
            int len = unpacker.unpackBinaryHeader();
            value.setData(unpacker.readPayload(len));
            break;
        case BOOLEAN:
            value.setData(unpacker.unpackBoolean());
            break;
        case FLOAT:
            value.setData(format == MessageFormat.FLOAT64 //
                    ? unpacker.unpackDouble() //
                    : unpacker.unpackFloat());
            break;
        case INTEGER:
            switch (format) {
            case INT8:
                value.setData(unpacker.unpackByte());
                break;
            case INT16:
            case UINT8:
                value.setData(unpacker.unpackShort());
                break;
            case UINT32:
            case INT64:
            case UINT64:
                value.setData(unpacker.unpackLong());
                break;
            default:
                value.setData(unpacker.unpackInt());
                break;
            }
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
        throw new BeanSerializationException("Cannot deserialize as BElement for format: " + format);
    }

    @Override
    public BElement deserialize(InputStream in) {
        try (var holder = UNPACKERS.get()) {
            return this.unpackAny(holder.reset(in));
        } catch (Exception e) {
            if (log.isDebugEnabled())
                log.debug("Error while deserializing input stream as BElement", e);

            throw new BeanSerializationException("Error while deserialize input stream", e);
        }
    }
}
