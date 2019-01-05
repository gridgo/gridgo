package io.gridgo.extras.typesafe;

import java.io.File;
import java.io.Reader;
import java.util.Optional;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import io.gridgo.bean.BElement;
import io.gridgo.config.impl.AbstractLocalConfigurator;

public class TypeSafeConfigurator extends AbstractLocalConfigurator {

    private Config config;

    private TypeSafeConfigurator(Config config) {
        this.config = config;
    }

    public static final TypeSafeConfigurator ofConfig(Config config) {
        return new TypeSafeConfigurator(config);
    }

    public static final TypeSafeConfigurator ofReader(Reader file) {
        return new TypeSafeConfigurator(ConfigFactory.parseReader(file));
    }

    public static final TypeSafeConfigurator ofResource(String resource) {
        return new TypeSafeConfigurator(ConfigFactory.parseResources(resource));
    }

    public static final TypeSafeConfigurator ofFile(File file) {
        return new TypeSafeConfigurator(ConfigFactory.parseFile(file));
    }

    public static final TypeSafeConfigurator ofFile(String file) {
        return ofFile(new File(file));
    }

    public static final TypeSafeConfigurator ofString(String s) {
        return new TypeSafeConfigurator(ConfigFactory.parseString(s));
    }

    @Override
    protected String generateName() {
        return "config.typesafe";
    }

    @Override
    protected Optional<BElement> resolve() {
        return Optional.of(BElement.ofAny(config.resolve().root().unwrapped()));
    }
}
