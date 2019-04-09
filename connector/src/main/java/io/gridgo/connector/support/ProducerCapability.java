package io.gridgo.connector.support;

public interface ProducerCapability {

    public boolean isCallSupported();

    public default boolean isSendSupported() {
        return true;
    }

    public default boolean isSendWithAckSupported() {
        return true;
    }
}
