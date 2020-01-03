package io.gridgo.connector.test;

import io.gridgo.connector.impl.AbstractConnector;
import io.gridgo.connector.support.annotations.ConnectorEndpoint;

@ConnectorEndpoint(scheme = "test99", syntax = "{bean}")
public class TestConnector extends AbstractConnector {

}
