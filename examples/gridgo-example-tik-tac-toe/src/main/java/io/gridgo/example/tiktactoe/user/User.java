package io.gridgo.example.tiktactoe.user;

import lombok.Builder;

@Builder
public class User {

	private String userName;
	private long sessionId;

	public String getUserName() {
		return userName;
	}

	public long getSessionId() {
		return sessionId;
	}
}
