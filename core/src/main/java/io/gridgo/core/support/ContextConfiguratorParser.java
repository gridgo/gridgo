package io.gridgo.core.support;

import io.gridgo.bean.BObject;
import io.gridgo.core.GridgoContext;
import io.gridgo.framework.support.Registry;

public interface ContextConfiguratorParser {

    public GridgoContext parse(Registry registry, BObject configObject);
}
