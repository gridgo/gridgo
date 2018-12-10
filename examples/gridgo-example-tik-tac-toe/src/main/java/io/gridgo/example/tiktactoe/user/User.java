package io.gridgo.example.tiktactoe.user;

import lombok.Builder;

@Builder
public class User {

    private String userName;
    private String sessionId;

    public String getUserName() {
        return userName;
    }

    public String getSessionId() {
        return sessionId;
    }
}
