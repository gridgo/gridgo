package io.gridgo.connector.test;

import io.gridgo.connector.impl.AbstractConnector;
import io.gridgo.connector.support.annotations.ConnectorEndpoint;

@ConnectorEndpoint(scheme = "empty", syntax = "")
public class EmptyConnector extends AbstractConnector {

}
