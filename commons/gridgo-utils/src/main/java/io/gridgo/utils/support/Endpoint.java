package io.gridgo.utils.support;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class Endpoint {

    private String address;

    private String protocol;

    private String host;

    private String nic;

    @Builder.Default
    private int port = -1;

    public String getResolvedAddress() {
        String nicStr = (this.nic == null || this.nic.isBlank()) ? "" : (this.nic + ";");
        return this.getProtocol() + "://" + nicStr + host + (port <= 0 ? "" : (":" + port));
    }

    @Override
    public String toString() {
        return this.getAddress();
    }
}
