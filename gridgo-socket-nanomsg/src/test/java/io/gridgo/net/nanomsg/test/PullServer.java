package io.gridgo.net.nanomsg.test;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;

import io.gridgo.socket.nanomsg.NNReceiver;
import io.gridgo.socket.nanomsg.NNSocket;
import io.gridgo.socket.nanomsg.NNSockets;

public class PullServer extends NNReceiver {

	public static void main(String[] args) throws InterruptedException {

		final PullServer server = new PullServer();
		final CountDownLatch doneSignal = new CountDownLatch(1);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			server.stop();
			doneSignal.countDown();
		}, "shutdown-thread"));

		server.start();
		doneSignal.await();
	}

	long lastRecvBytes = 0;
	long lastRecvMessageCount = 0;
	private Thread monitorThread;

	public PullServer() {
		super(2048);
	}

	@Override
	protected void onStartSuccess() {
		lastRecvMessageCount = 0;
		this.monitorThread = new Thread(this::monitor);
		this.monitorThread.start();
	}

	private void monitor() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}

			long newRecvMessageCount = this.getTotalRecvMsg();
			long newRecvBytes = this.getTotalRecvBytes();
			if (newRecvBytes > lastRecvBytes) {
				long deltaRecvBytes = newRecvBytes - lastRecvBytes;
				long deltaRecvMsgCount = newRecvMessageCount - lastRecvMessageCount;

				lastRecvBytes = newRecvBytes;
				lastRecvMessageCount = newRecvMessageCount;

				System.out.printf("Message rate: %,d (msg/s) at throughput: %,.2f KB/s \n", deltaRecvMsgCount,
						Double.valueOf(deltaRecvBytes) / 1024);
			}
		}
	}

	@Override
	protected void onFinally() {
		if (this.monitorThread != null) {
			if (this.monitorThread.isAlive()) {
				this.monitorThread.interrupt();
			}
			this.monitorThread = null;
		}
	}

	@Override
	protected NNSocket createSocket() {
		NNSocket socket = NNSockets.createPullSocket("tcp://127.0.0.1:8888");
		if (!socket.setRecvTimeout(100)) {
			throw new RuntimeException("Cannot set recvTimeout for socket " + socket.getAddress());
		} else if (!socket.bind()) {
			throw new RuntimeException("Cannot bind socket to " + socket.getAddress());
		}
		return socket;
	}

	@Override
	protected void onRecv(int length, ByteBuffer buffer) {
		// System.out.printf("Received %d bytes\n", length);
	}

}
