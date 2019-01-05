package io.gridgo.extras.yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.Optional;
import java.util.function.Function;

import org.yaml.snakeyaml.Yaml;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;
import io.gridgo.config.impl.AbstractLocalConfigurator;

public class YamlConfigurator extends AbstractLocalConfigurator {

    private Yaml yaml = new Yaml();

    private Function<Yaml, BObject> loader;

    private YamlConfigurator() {
    }

    public YamlConfigurator(Yaml config) {
        this.yaml = config;
    }

    private YamlConfigurator load(Function<Yaml, BObject> loader) {
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

    public static final YamlConfigurator ofResource(String resource) {
        return ofEmpty().load(yaml -> yaml.load(Yaml.class.getResourceAsStream(resource)));
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
        return Optional.of(loader.apply(yaml));
    }

    @Override
    protected String generateName() {
        return "config.yaml";
    }
}
