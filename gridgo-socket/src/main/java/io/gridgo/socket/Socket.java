package io.gridgo.socket;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

public interface Socket {

	boolean isAlive();

	void close();

	void connect();

	void bind();

	String getAddress();

	void send(ByteBuffer buffer);

	default void send(byte[] bytes) {
		this.send(ByteBuffer.wrap(bytes));
	}

	void setRecvConsumer(Consumer<ByteBuffer> consumer);
}
