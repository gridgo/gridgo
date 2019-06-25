package io.gridgo.connector.support.config.impl;

import java.util.Map;
import java.util.Properties;

import io.gridgo.connector.support.config.ConnectorConfig;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DefaultConnectorConfig implements ConnectorConfig {

    private String scheme;

    private String nonQueryEndpoint;
    
    private String originalEndpoint;

    private String remaining;
    
    private String connectorCategory;

    private Map<String, Object> parameters;

    private Properties placeholders;
}
