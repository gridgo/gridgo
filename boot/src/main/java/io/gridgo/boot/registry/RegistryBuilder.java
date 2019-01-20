package io.gridgo.boot.registry;

import java.io.File;
import java.util.ArrayList;

import io.gridgo.framework.support.Builder;
import io.gridgo.framework.support.Registry;
import io.gridgo.framework.support.impl.MultiSourceRegistry;
import io.gridgo.framework.support.impl.PropertiesFileRegistry;
import io.gridgo.framework.support.impl.SystemEnvRegistry;
import io.gridgo.framework.support.impl.SystemPropertyRegistry;

public class RegistryBuilder implements Builder<Registry> {

    private static final String APP_PROPERTIES_FILE = "application.properties";

    private String profile;

    public RegistryBuilder setEnv(String profile) {
        this.profile = profile;
        return this;
    }

    @Override
    public Registry build() {
        var registries = new ArrayList<Registry>();
        registries.add(new SystemPropertyRegistry());
        registries.add(new SystemEnvRegistry());

        var envFile = getProfile();
        if (envFile != null)
            registries.add(new PropertiesFileRegistry(getConfigFile(envFile + ".properties")));
        var file = new File(APP_PROPERTIES_FILE);
        if (file.exists())
            registries.add(new PropertiesFileRegistry(getConfigFile(APP_PROPERTIES_FILE)));

        return new MultiSourceRegistry(registries.toArray(new Registry[0]));
    }

    private String getConfigFile(String envFile) {
        return "config/" + envFile;
    }

    private String getProfile() {
        if (profile != null)
            return profile;
        var systemProfile = System.getProperty("gridgo.profile");
        if (systemProfile != null)
            return systemProfile;
        systemProfile = System.getenv("gridgo_profile");
        if (systemProfile != null)
            return systemProfile;
        return null;
    }
}
