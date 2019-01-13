package io.gridgo.extras.yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.yaml.snakeyaml.Yaml;

import io.gridgo.bean.BElement;
import io.gridgo.config.impl.AbstractLocalConfigurator;

public class YamlConfigurator extends AbstractLocalConfigurator {

    private Yaml yaml = new Yaml();

    private Function<Yaml, Map<String, Object>> loader = ignored -> Collections.emptyMap();

    private YamlConfigurator() {
        // Nothing to do here
    }

    public YamlConfigurator(Yaml config) {
        this.yaml = config;
    }

    private YamlConfigurator load(Function<Yaml, Map<String, Object>> loader) {
        this.loader = loader;
        return this;
    }

    public static final YamlConfigurator ofEmpty() {
        return new YamlConfigurator();
    }

    public static final YamlConfigurator ofConfig(Yaml config) {
        return new YamlConfigurator(config);
    }

    public static final YamlConfigurator ofReader(Reader reader) {
        return ofEmpty().load(yaml -> yaml.load(reader));
    }

    public static final YamlConfigurator ofStream(InputStream is) {
        return ofEmpty().load(yaml -> yaml.load(is));
    }

    public static final YamlConfigurator ofResource(String resource) {
        var classloader = Thread.currentThread().getContextClassLoader();
        return ofStream(classloader.getResourceAsStream(resource));
    }

    public static final YamlConfigurator ofFile(File file) throws FileNotFoundException {
        return ofReader(new FileReader(file));
    }

    public static final YamlConfigurator ofFile(String file) throws FileNotFoundException {
        return ofFile(new File(file));
    }

    public static final YamlConfigurator ofString(String s) {
        return ofEmpty().load(yaml -> yaml.load(s));
    }

    @Override
    protected Optional<BElement> resolve() {
        return Optional.of(BElement.ofAny(loader.apply(yaml)));
    }

    @Override
    protected String generateName() {
        return "config.yaml";
    }
}
