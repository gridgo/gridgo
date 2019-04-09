package io.gridgo.connector.support.config;

import java.util.Map;
import java.util.Properties;

public interface ConnectorConfig {
    
    public String getOriginalEndpoint();

    public String getNonQueryEndpoint();

    public Map<String, Object> getParameters();

    public Properties getPlaceholders();

    public String getRemaining();

    public String getScheme();
}
