package io.gridgo.bean.serialization.msgpack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.buffer.OutputStreamBufferOutput;

class PackerAndBuffer implements AutoCloseable {
    private final OutputStreamBufferOutput output = new OutputStreamBufferOutput(new ByteArrayOutputStream(0));
    private final MessagePacker packer = MessagePack.newDefaultPacker(this.output);

    @Override
    public void close() throws IOException {
        this.packer.close();
        this.output.close();
    }

    public MessagePacker reset(OutputStream out) throws IOException {
        this.output.reset(out);
        return this.packer;
    }
}