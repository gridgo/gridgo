package io.gridgo.config.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Optional;

import io.gridgo.bean.BElement;
import io.gridgo.utils.exception.RuntimeIOException;

public class JsonConfigurator extends AbstractLocalConfigurator {

    private BElement config;

    public JsonConfigurator(BElement config) {
        this.config = config;
    }

    @Override
    protected Optional<BElement> resolve() {
        return Optional.of(config);
    }

    @Override
    protected String generateName() {
        return "config.json";
    }

    public static final JsonConfigurator ofReader(Reader reader) {
        return new JsonConfigurator(BElement.ofJson(reader));
    }

    public static final JsonConfigurator ofStream(InputStream stream) {
        return new JsonConfigurator(BElement.ofJson(stream));
    }

    public static final JsonConfigurator ofResource(String resource) {
        var classloader = Thread.currentThread().getContextClassLoader();
        return ofStream(classloader.getResourceAsStream(resource));
    }

    public static final JsonConfigurator ofFile(File file) {
        try (var is = new FileInputStream(file)) {
            return ofStream(is);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    public static final JsonConfigurator ofFile(String file) {
        return ofFile(new File(file));
    }

    public static final JsonConfigurator ofString(String s) {
        return new JsonConfigurator(BElement.ofJson(s));
    }
}
