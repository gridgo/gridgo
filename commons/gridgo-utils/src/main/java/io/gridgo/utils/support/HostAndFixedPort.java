package io.gridgo.utils.support;

public class HostAndFixedPort extends HostAndPort {

    public HostAndFixedPort(final int port) {
        super(port);
    }

    public HostAndFixedPort(String host, final int port) {
        super(host, port);
    }

    @Override
    public void setPort(int port) {
        throw new UnsupportedOperationException("Port cannot be changed");
    }
}
