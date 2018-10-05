package io.gridgo.socket;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SocketOptions {

	private SocketType socketType;

	private String address;

	private boolean passive;
}
