package io.gridgo.utils.wrapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import static org.junit.Assert.*;

public class ByteBufferOutputStreamTest {

    int capacity = 2;

    ByteBuffer byteBuffer = ByteBuffer.allocate(capacity);
    ByteBufferOutputStream output = new ByteBufferOutputStream(byteBuffer);

    @Before
    public void setUp() {
        this.byteBuffer = ByteBuffer.allocate(capacity);
        this.output = new ByteBufferOutputStream(byteBuffer);

    }

    @After
    public void tearDown() {
    }

    @Test
    public void buffer() {
        assertSame(byteBuffer, output.buffer());
        assertSame(byteBuffer, output.getBuffer());
    }

    @Test (expected = BufferOverflowException.class)
    public void write_ShouldSuccessThenThrowException_WhenWriteToNoAutoExpandOutputStream() {
        output.write(1);
        output.write(2);
        output.write(3);
    }

    @Test
    public void write_ShouldSuccess_WhenAutoExpandBuffer() {
        output = new ByteBufferOutputStream(byteBuffer, true);
        output.write(1);
        output.write(2);
        output.write(3);
    }

    @Test(expected = BufferOverflowException.class)
    public void write_ShouldThrowException_WhenOutputIsDirectBufferCannotExpand() {
        byteBuffer = ByteBuffer.allocateDirect(capacity);
        output = new ByteBufferOutputStream(byteBuffer, true);
        output.write(1);
        output.write(2);
        output.write(3);
    }

    @Test (expected = BufferOverflowException.class)
    public void writeBytes_ShouldSuccessThenThrowException_WhenWriteToNoAutoExpandOutputStream() {
        output.write(new byte[capacity+1], 0, capacity + 1);
    }

    @Test
    public void writeBytes_ShouldSuccess_WhenAutoExpandBuffer() {
        output = new ByteBufferOutputStream(byteBuffer, true);
        output.write(new byte[capacity+1], 0, capacity + 1);

    }

    @Test(expected = BufferOverflowException.class)
    public void writeBytes_ShouldThrowException_WhenOutputIsDirectBufferCannotExpand() {
        byteBuffer = ByteBuffer.allocateDirect(capacity);
        output = new ByteBufferOutputStream(byteBuffer, true);
        output.write(new byte[capacity+1], 0, capacity + 1);
    }
}