package io.gridgo.bean.serialization.msgpack;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.core.buffer.InputStreamBufferInput;

class UnpackerAndBuffer implements AutoCloseable {
    private final InputStreamBufferInput input = new InputStreamBufferInput(new ByteArrayInputStream(new byte[0]));
    private final MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(this.input);

    @Override
    public void close() throws IOException {
        this.unpacker.close();
        this.input.close();
    }

    public MessageUnpacker reset(InputStream in) throws IOException {
        this.input.reset(in);
        return this.unpacker;
    }
}