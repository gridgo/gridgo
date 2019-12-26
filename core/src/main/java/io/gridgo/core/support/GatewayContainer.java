package io.gridgo.core.support;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import io.gridgo.core.Gateway;
import io.gridgo.core.support.subscription.GatewaySubscription;
import io.gridgo.core.support.template.ProducerTemplate;

/**
 * Represents a gateway container.
 */
public interface GatewayContainer {

    /**
     * Open a new gateway.
     *
     * @param name the gateway name
     */
    public default GatewaySubscription openGateway(String name) {
        return openGateway(name, ProducerJoinMode.SINGLE);
    }

    /**
     * Open a new gateway with a specific join mode.
     *
     * @param name     the gateway name
     * @param joinMode the join mode
     */
    public default GatewaySubscription openGateway(String name, ProducerJoinMode joinMode) {
        return openGateway(name, ProducerTemplate.create(joinMode));
    }

    /**
     * Open a new gateway with a specific producer template.
     *
     * @param name             the gateway name
     * @param producerTemplate the producer template
     */
    public GatewaySubscription openGateway(String name, ProducerTemplate producerTemplate);

    /**
     * Close a gateway, stopping it from accepting or sending messages.
     *
     * @param name the gateway name to be closed
     * @return the closed gateway, wrapped in Optional
     */
    public Optional<Gateway> closeGateway(String name);

    /**
     * Find a gateway by name.
     *
     * @param name the gateway name
     * @return the gateway, wrapped in Optional
     */
    public Optional<Gateway> findGateway(String name);

    /**
     * Get the list of opened gateways.
     *
     * @return the list of opened gateways
     */
    public Collection<GatewaySubscription> getGateways();

    /**
     * Get all opened gateways with name.
     *
     * @return opened gateways with name
     */
    public Map<String, GatewaySubscription> getGatewaysWithNames();
}
