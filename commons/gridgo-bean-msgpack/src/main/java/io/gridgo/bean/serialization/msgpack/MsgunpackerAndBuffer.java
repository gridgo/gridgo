package io.gridgo.bean.serialization.msgpack;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.core.buffer.ByteBufferInput;
import org.msgpack.core.buffer.InputStreamBufferInput;

import io.gridgo.utils.wrapper.ByteBufferInputStream;

class MsgunpackerAndBuffer implements AutoCloseable {

    private static final boolean IS_BIG_ENDIAN = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN;

    private static final ByteBuffer DEFAULT_EMPTY_BUFFER = ByteBuffer.allocate(0);
    private static final ByteArrayInputStream DEFAULT_EMPTY_IS = new ByteArrayInputStream(new byte[0]);

    private InputStreamBufferInput streamBufferInput = new InputStreamBufferInput(DEFAULT_EMPTY_IS);
    private ByteBufferInput byteBufferInput = new ByteBufferInput(DEFAULT_EMPTY_BUFFER);
    private MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(streamBufferInput);

    @Override
    public void close() throws IOException {
        streamBufferInput.close();
        byteBufferInput.close();
        unpacker.close();
    }

    public MessageUnpacker reset(InputStream in) throws IOException {
        ByteBuffer buffer;
        if (ByteBufferInputStream.class.isInstance(in)
                && (!(buffer = ((ByteBufferInputStream) in).getBuffer()).isDirect() //
                        || IS_BIG_ENDIAN)) {

            byteBufferInput.reset(buffer);
            unpacker.reset(byteBufferInput);
        } else {
            streamBufferInput.reset(in);
            unpacker.reset(streamBufferInput);
        }
        return unpacker;
    }
}