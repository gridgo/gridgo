package io.gridgo.utils.wrapper;

import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ByteBufferInputStreamTest {

    @Test
    public void read_ShouldReturnValue_WhenHaveRemaining() {
        int capacity = 10;
        var byteBuffer = ByteBuffer.allocate(capacity);
        for (int i = 0; i < capacity; i++) {
            byteBuffer.put((byte) 0x64);
        }
        byteBuffer.position(0);


        ByteBufferInputStream inputStream = new ByteBufferInputStream(byteBuffer);

        assertEquals(0x64, inputStream.read());
    }

    @Test
    public void read_ShouldReturnNegativeOne_WhenNotHaveRemaining() {
        int capacity = 1;
        var byteBuffer = ByteBuffer.allocate(capacity);
        for (int i = 0; i < capacity; i++) {
            byteBuffer.put((byte) 0x64);
        }
        byteBuffer.position(0);

        ByteBufferInputStream inputStream = new ByteBufferInputStream(byteBuffer);

        assertEquals(0x64, inputStream.read());
        assertEquals(-1, inputStream.read());
        assertEquals(-1, inputStream.read());
    }

    @Test
    public void readByteArray_ShouldSuccess_When() {
        int capacity = 4;
        var byteBuffer = ByteBuffer.allocate(capacity);
        for (int i = 0; i < capacity; i++) {
            byteBuffer.put((byte) i);
        }
        byteBuffer.position(0);

        ByteBufferInputStream inputStream = new ByteBufferInputStream(byteBuffer);
        byte[] out = new byte[4];
        assertEquals(4, inputStream.read(out, 0, 4));

        assertArrayEquals(new byte[] {0, 1, 2, 3}, out);

        assertEquals(-1, inputStream.read(out, 0, 1));
    }

}