package io.gridgo.boot.data.test.producer;

import io.gridgo.boot.support.annotations.Connector;
import io.gridgo.boot.support.annotations.Gateway;

@Gateway("mysql")
@Connector("jdbc:mysql://localhost:3306/test?user=root")
public class MySqlGateway {
    
}
