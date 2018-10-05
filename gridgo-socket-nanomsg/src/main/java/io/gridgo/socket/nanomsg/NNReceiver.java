package io.gridgo.socket.nanomsg;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

import io.gridgo.utils.helper.AbstractStartable;
import lombok.Getter;

public abstract class NNReceiver extends AbstractStartable {

	private Thread poller;

	private CountDownLatch startedSignal;
	private Exception startException;

	private final int bufferSize;

	@Getter
	private long totalRecvBytes = 0;

	@Getter
	private long totalRecvMsg = 0;

	protected NNReceiver(int bufferSize) {
		if (bufferSize < 0) {
			throw new IllegalArgumentException("Buffer size cannot be negative");
		}
		this.bufferSize = bufferSize;
	}

	protected NNReceiver() {
		this(1024);
	}

	@Override
	protected final void onStart(CompletableFuture<Void> future) {
		if (this.poller != null) {
			throw new IllegalStateException("Poller cannot exist on start");
		}

		this.startedSignal = new CountDownLatch(1);
		this.startException = null;

		this.poller = new Thread(this::poll);
		this.poller.start();

		try {
			startedSignal.await();
			if (startException != null) {
				future.completeExceptionally(startException);
			} else {
				this.totalRecvBytes = 0;
				this.totalRecvMsg = 0;
				this.onStartSuccess();
			}
		} catch (Exception e) {
			future.completeExceptionally(e);
		}
	}

	protected void onStartSuccess() {

	}

	@Override
	protected final void onStop() {
		if (this.poller != null) {
			if (this.poller.isAlive()) {
				this.poller.interrupt();
			}
			this.poller = null;
			this.onFinally();
		}
	}

	protected void onFinally() {

	}

	protected abstract NNSocket createSocket();

	private void poll() {
		NNSocket socket = null;
		try {
			socket = this.createSocket();
		} catch (Exception e) {
			startException = e;
		} finally {
			if (this.startedSignal != null) {
				this.startedSignal.countDown();
			}
			if (startException != null) {
				return;
			}
		}

		if (socket != null) {
			Thread.currentThread().setName(socket.getAddress() + " poller-thread");

			final ByteBuffer buffer = ByteBuffer.allocateDirect(this.bufferSize);
			while (!Thread.currentThread().isInterrupted()) {
				buffer.clear();
				int rc = socket.receive(buffer);
				if (rc < 0) {
					if (Thread.currentThread().isInterrupted()) {
						break;
					} else {
						// socket timeout, continue event loop
					}
				} else if (rc > 0) {
					totalRecvBytes += rc;
					totalRecvMsg++;
					try {
						buffer.rewind();
						this.onRecv(rc, buffer);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			socket.close();
		}
	}

	protected abstract void onRecv(int length, ByteBuffer buffer);
}
