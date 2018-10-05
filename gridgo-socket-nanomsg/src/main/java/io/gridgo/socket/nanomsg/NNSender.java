package io.gridgo.socket.nanomsg;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

import io.gridgo.utils.helper.AbstractStartable;

public abstract class NNSender extends AbstractStartable {

	private NNSocket socket;

	protected abstract NNSocket createSocket();

	private final AtomicLong totalSentBytes = new AtomicLong(0);
	private final AtomicLong totalSentMsg = new AtomicLong(0);

	public final long getTotalSentMsg() {
		return totalSentMsg.get();
	}

	public final long getTotalSentBytes() {
		return this.totalSentBytes.get();
	}

	@Override
	protected final void onStart(CompletableFuture<Void> future) {
		try {
			this.totalSentBytes.set(0);
			this.totalSentMsg.set(0);

			this.socket = this.createSocket();
			this.onStartSuccess();
			future.complete(null);
		} catch (Throwable e) {
			future.completeExceptionally(e);
		}
	}

	protected void onStartSuccess() {

	}

	@Override
	protected final void onStop() {
		this.socket.close();
		this.socket = null;
	}

	protected void send(ByteBuffer buffer) {
		if (this.isStarted()) {
			final int length = buffer.remaining();

			this.socket.send(buffer);

			this.totalSentBytes.addAndGet(length);
			this.totalSentMsg.incrementAndGet();
		} else {
			throw new IllegalStateException("Not started");
		}
	}
}
