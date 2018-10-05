package io.gridgo.socket;

public enum SocketType {

	DUPLEX, RPC, PUSH, PULL, SUB, PUB;

	public static SocketType forName(String name) {
		for (SocketType value : values()) {
			if (value.name().equalsIgnoreCase(name)) {
				return value;
			}
		}
		return null;
	}
}
