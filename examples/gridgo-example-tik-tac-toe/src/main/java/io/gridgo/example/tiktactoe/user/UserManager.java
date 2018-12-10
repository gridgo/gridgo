package io.gridgo.example.tiktactoe.user;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;

import org.cliffc.high_scale_lib.NonBlockingHashMap;

import lombok.NonNull;

public class UserManager {

    private final Map<String, User> users = new NonBlockingHashMap<>();
    private final Map<String, String> userNameLookUpMap = new NonBlockingHashMap<>();

    private final AtomicLong listenerIdSeed = new AtomicLong(0);
    private final Map<Long, BiConsumer<UserEventType, User>> listeners = new NonBlockingHashMap<>();

    public long addListener(BiConsumer<UserEventType, User> listener) {
        long listenerId = listenerIdSeed.getAndIncrement();
        this.listeners.put(listenerId, listener);
        return listenerId;
    }

    public void removeListener(long listenerId) {
        this.listeners.remove(listenerId);
    }

    public void addUser(@NonNull String userName, String routingId) {
        User currSession = this.users.putIfAbsent(routingId, User.builder() //
                                                                 .userName(userName) //
                                                                 .sessionId(routingId) //
                                                                 .build());

        if (currSession != null) {
            throw new UserException("User with name " + userName + " is already logged in");
        } else {
            this.userNameLookUpMap.put(userName, routingId);
            this.listeners.values().forEach(listener -> {
                listener.accept(UserEventType.LOGGED_IN, this.users.get(routingId));
            });
        }
    }

    public void removeUserBySession(String routingId) {
        User currSession = this.users.remove(routingId);
        if (currSession != null) {
            this.userNameLookUpMap.remove(currSession.getUserName());
            this.listeners.values().forEach(listener -> {
                listener.accept(UserEventType.LOGGED_OUT, currSession);
            });
        }
    }

    public void removeUser(@NonNull String userName) {
        User user = this.getUser(userName);
        if (user != null) {
            this.removeUser(user.getSessionId());
        }
    }

    public User getUserBySession(String sessionId) {
        return this.users.get(sessionId);
    }

    public User getUser(@NonNull String userName) {
        String sessionId = this.userNameLookUpMap.get(userName);
        if (sessionId != null) {
            return this.users.get(sessionId);
        }
        return null;
    }

    public boolean isLoggedIn(@NonNull String userName) {
        String sessionId = this.userNameLookUpMap.get(userName);
        if (sessionId != null) {
            return this.users.containsKey(sessionId);
        }
        return false;
    }

    public Collection<User> getAllUser() {
        return this.users.values();
    }
}
