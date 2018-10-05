package io.gridgo.net.nanomsg.test;

import java.nio.ByteBuffer;

import io.gridgo.socket.nanomsg.NNSender;
import io.gridgo.socket.nanomsg.NNSocket;
import io.gridgo.socket.nanomsg.NNSockets;

public class PushClient extends NNSender {

	public static void main(String[] args) throws InterruptedException {
		final PushClient client = new PushClient();
		
		Runtime.getRuntime().addShutdownHook(new Thread(client::stop, "shutdown-thread"));
		client.start();

		int numMessages = (int) 1e6;
		int messageSize = 2048;

		final ByteBuffer buffer = ByteBuffer.allocateDirect(messageSize);
		byte bval = 111;
		for (int i = 0; i < messageSize; ++i) {
			buffer.put(i, bval);
		}

		long start = System.nanoTime();
		for (int i = 0; i < numMessages; i++) {
			buffer.rewind();
			client.send(buffer);
		}

		double elaspedSeconds = Double.valueOf(System.nanoTime() - start) / 1e9;

		System.out.printf("Total sent message: %,d\n", client.getTotalSentMsg());
		System.out.printf("Message rate: %,.2f (msg/s)\n",
				(Double.valueOf(client.getTotalSentMsg()).doubleValue() / elaspedSeconds));
		System.out.printf("Throughput: %,.2f (KB/s)\n",
				(Double.valueOf(client.getTotalSentBytes()).doubleValue() / 1024 / elaspedSeconds));

		System.exit(0);
	}

	@Override
	protected NNSocket createSocket() {
		NNSocket socket = NNSockets.createPushSocket("tcp://127.0.0.1:8888");
		if (!socket.connect()) {
			throw new RuntimeException("Cannot connect to: " + socket.getAddress());
		}
		return socket;
	}
}
