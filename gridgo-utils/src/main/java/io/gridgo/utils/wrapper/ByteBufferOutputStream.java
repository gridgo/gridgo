package io.gridgo.utils.wrapper;

import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import lombok.Getter;

public class ByteBufferOutputStream extends OutputStream {

	private static final float REALLOCATION_FACTOR = 1.1f;

	@Getter
	private ByteBuffer buffer;

	private boolean autoExpandBuffer = false;

	public ByteBufferOutputStream(ByteBuffer buffer) {
		this(buffer, false);
	}

	public ByteBufferOutputStream(ByteBuffer buffer, boolean autoExpandBuffer) {
		this.buffer = buffer;
		this.autoExpandBuffer = autoExpandBuffer;
	}

	public void write(int b) {
		if (buffer.remaining() < 1) {
			if (this.buffer.isDirect() || !this.autoExpandBuffer) {
				throw new BufferOverflowException();
			} else {
				expandBuffer(buffer.capacity() + 1);
			}
		}
		buffer.put((byte) b);
	}

	public void write(byte[] bytes, int off, int len) {
		if (buffer.remaining() < len) {
			if (this.buffer.isDirect() || !this.autoExpandBuffer) {
				throw new BufferOverflowException();
			} else {
				expandBuffer(buffer.capacity() + len);
			}
		}
		buffer.put(bytes, off, len);
	}

	public ByteBuffer buffer() {
		return buffer;
	}

	private void expandBuffer(int size) {
		// only support if buffer backed by array (non-direct bytebuffer)
		int expandSize = Math.max((int) (buffer.capacity() * REALLOCATION_FACTOR), size);
		ByteBuffer temp = ByteBuffer.allocate(expandSize);
		temp.put(buffer.array(), buffer.arrayOffset(), buffer.position());
		buffer = temp;
	}
}