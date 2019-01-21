package io.gridgo.boot.config;

import io.gridgo.boot.support.exceptions.ResourceNotFoundException;
import io.gridgo.config.impl.AbstractConfigurator;
import io.gridgo.config.impl.AbstractLocalConfigurator;
import io.gridgo.config.impl.JsonConfigurator;
import io.gridgo.extras.typesafe.TypeSafeConfigurator;
import io.gridgo.extras.yaml.YamlConfigurator;

public class ResourceConfigurator extends AbstractConfigurator {

    private static final String[] RESOURCES = new String[] { //
            "gridgo-context.conf", //
            "gridgo-context.yaml", //
            "gridgo-context.yml", //
            "gridgo-context.json" };

    private AbstractLocalConfigurator configurator;

    public ResourceConfigurator() {
        this.configurator = findConfigurator();
        this.configurator.subscribe(this::publish);
    }

    private AbstractLocalConfigurator findConfigurator() {
        for (var resource : RESOURCES) {
            var url = getResource(resource);
            if (url == null)
                continue;
            if (resource.endsWith(".conf"))
                return TypeSafeConfigurator.ofResource(resource);
            if (resource.endsWith(".yml") || resource.endsWith(".yaml"))
                return YamlConfigurator.ofResource(resource);
            if (resource.endsWith(".json"))
                return JsonConfigurator.ofResource(resource);
        }
        throw new ResourceNotFoundException("No available resource not found");
    }

    private Object getResource(String resource) {
        return Thread.currentThread().getContextClassLoader().getResource(resource);
    }

    @Override
    protected void onStart() {
        configurator.start();
    }

    @Override
    protected void onStop() {
        configurator.stop();
    }

    @Override
    protected String generateName() {
        return "config.resource";
    }
}
