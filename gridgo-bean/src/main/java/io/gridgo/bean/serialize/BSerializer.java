package io.gridgo.bean.serialize;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import io.gridgo.bean.BElement;
import io.gridgo.utils.wrapper.ByteBufferInputStream;
import io.gridgo.utils.wrapper.ByteBufferOutputStream;

public interface BSerializer {

	void serialize(BElement element, OutputStream out);

	BElement deserialize(InputStream in);

	default void serialize(BElement element, ByteBuffer out) {
		this.serialize(element, new ByteBufferOutputStream(out));
	}

	default BElement deserialize(ByteBuffer buffer) {
		return this.deserialize(new ByteBufferInputStream(buffer));
	}

	default BElement deserialize(byte[] bytes) {
		return this.deserialize(new ByteArrayInputStream(bytes));
	}

	default int getDefaultOutputCapactity() {
		return 1024;
	}
}
