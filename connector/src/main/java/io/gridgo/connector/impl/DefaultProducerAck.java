package io.gridgo.connector.impl;

import io.gridgo.connector.ProducerAck;
import io.gridgo.connector.support.config.ConnectorContext;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DefaultProducerAck implements ProducerAck {

    private ConnectorContext context;
}
