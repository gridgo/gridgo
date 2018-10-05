package io.gridgo.socket.nanomsg;

import java.nio.ByteBuffer;

import org.nanomsg.NanoLibrary;

import lombok.AccessLevel;
import lombok.Getter;

public class NNSocket {

	static final NanoLibrary nanomsg = new NanoLibrary();

	@Getter(AccessLevel.PROTECTED)
	private final int id;

	@Getter
	private final String address;

	NNSocket(int type, String address) {
		if (address == null) {
			throw new NullPointerException("Cannot create socket with null address");
		}

		this.id = nanomsg.nn_socket(nanomsg.AF_SP, type);
		if (this.getId() < 0) {
			throw new RuntimeException("Cannot create socket with type " + type);
		}

		this.address = address;
	}

	public boolean connect() {
		return nanomsg.nn_connect(id, address) >= 0;
	}

	public boolean bind() {
		return nanomsg.nn_bind(id, address) >= 0;
	}

	public boolean setTcpNodelay(boolean nodelay) {
		return nanomsg.nn_setsockopt_int(id, nanomsg.NN_TCP, nanomsg.NN_TCP_NODELAY, nodelay ? 1 : 0) >= 0;
	}

	public boolean setRecvTimeout(int recvTimeout) {
		return nanomsg.nn_setsockopt_int(id, nanomsg.NN_SOL_SOCKET, nanomsg.NN_RCVTIMEO, recvTimeout) >= 0;
	}

	public boolean setSendTimeout(int sendTimeout) {
		return nanomsg.nn_setsockopt_int(id, nanomsg.NN_SOL_SOCKET, nanomsg.NN_SNDTIMEO, sendTimeout) >= 0;
	}

	public boolean close() {
		return nanomsg.nn_close(id) >= 0;
	}

	public int send(byte[] bytes) {
		return nanomsg.nn_sendbyte(id, bytes, 0);
	}

	public int send(ByteBuffer bb) {
		return nanomsg.nn_send(id, bb, 0);
	}

	public int receive(ByteBuffer buffer, boolean block) {
		return nanomsg.nn_recv(id, buffer, block ? 0 : nanomsg.NN_DONTWAIT);
	}

	public int receive(ByteBuffer buffer) {
		return this.receive(buffer, true);
	}

	public byte[] receive() {
		return nanomsg.nn_recvbyte(id, 0);
	}
}
