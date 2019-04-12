package io.gridgo.dummy.test;

import io.gridgo.connector.impl.AbstractConnector;
import io.gridgo.connector.support.annotations.ConnectorEndpoint;

@ConnectorEndpoint(scheme = "dummy", syntax = "{type}:{transport}://{host}:{port}")
public class DummyConnector extends AbstractConnector {
}
