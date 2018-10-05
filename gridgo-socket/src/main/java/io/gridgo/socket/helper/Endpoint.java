package io.gridgo.socket.helper;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Endpoint {

	private String address;

	private String protocol;

	private String host;

	@Builder.Default
	private int port = -1;

	public String getRebuildAddress() {
		return this.getProtocol() + "://" + host + (port <= 0 ? "" : (":" + port));
	}
}
