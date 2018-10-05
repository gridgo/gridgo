package org.nanomsg;

public class NNPollFD {
    public final int fd;
    public final int events;
    public int revents;

    public NNPollFD(int fd, int events) {
        this.fd = fd; this.events = events; this.revents = 0;
    }
};