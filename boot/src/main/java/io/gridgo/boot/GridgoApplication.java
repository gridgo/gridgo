package io.gridgo.boot;

import io.gridgo.boot.config.ResourceConfigurator;
import io.gridgo.boot.registry.RegistryBuilder;
import io.gridgo.core.GridgoContext;
import io.gridgo.core.impl.ConfiguratorContextBuilder;
import io.gridgo.framework.impl.AbstractComponentLifecycle;

public class GridgoApplication extends AbstractComponentLifecycle {

    private GridgoContext context;

    private String appName;

    public GridgoApplication() {
        var registry = new RegistryBuilder().build();
        var configurator = new ResourceConfigurator();
        this.context = new ConfiguratorContextBuilder().setRegistry(registry) //
                                                       .setConfigurator(configurator) //
                                                       .build();
        this.appName = this.context.getName();
    }

    @Override
    protected void onStart() {
        context.start();
    }

    @Override
    protected void onStop() {
        context.stop();
    }

    @Override
    protected String generateName() {
        return "app." + appName;
    }
}
