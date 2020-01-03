package io.gridgo.core.support.config;

import io.gridgo.bean.BObject;
import io.gridgo.core.support.config.ConfiguratorNode.RootNode;

public interface ContextConfiguratorParser {

    public RootNode parse(BObject configObject);
}
