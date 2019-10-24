package io.gridgo.connector.impl.resolvers;

import static io.gridgo.utils.ClasspathUtils.scanForSubTypes;

import java.util.HashMap;
import java.util.Map;

import io.gridgo.connector.Connector;
import io.gridgo.connector.ConnectorResolver;
import io.gridgo.connector.support.annotations.ConnectorEndpoint;
import io.gridgo.connector.support.config.ConnectorContext;
import io.gridgo.connector.support.exceptions.UnsupportedSchemeException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClasspathConnectorResolver implements ConnectorResolver {

    private static final String DEFAULT_PACKAGE = "io.gridgo.connector";

    private Map<String, Class<? extends Connector>> classMappings = new HashMap<>();

    public ClasspathConnectorResolver() {
        this(DEFAULT_PACKAGE);
    }

    public ClasspathConnectorResolver(final @NonNull String... packages) {
        resolveClasspaths(packages);
    }

    private void registerConnectorClass(Class<? extends Connector> clzz) {
        var endpointAnnotations = clzz.getAnnotationsByType(ConnectorEndpoint.class);
        if (endpointAnnotations.length != 1) {
            return;
        }
        var endpoint = endpointAnnotations[0];
        String[] schemes = endpoint.scheme().split(",");
        for (String scheme : schemes) {
            scheme = scheme.trim();
            if (classMappings.containsKey(scheme)) {
                if (log.isWarnEnabled()) {
                    log.warn("Duplicate scheme {} when processing connector {}. Existing connector: {}", scheme,
                            clzz.getClass().getName(), classMappings.get(scheme).getName());
                }
            } else {
                if (log.isInfoEnabled()) {
                    log.info("Register scheme {} with connector", scheme, clzz.getClass().getName());
                }
                classMappings.put(scheme, clzz);
            }
        }
    }

    @Override
    public Connector resolve(final @NonNull String endpoint, ConnectorContext context) {
        String scheme = endpoint, remaining = "";
        int schemeIdx = endpoint.indexOf(':');
        if (schemeIdx != -1) {
            scheme = endpoint.substring(0, schemeIdx);
            remaining = endpoint.substring(schemeIdx + 1);
        }

        var clazz = classMappings.get(scheme);
        if (clazz == null)
            throw new UnsupportedSchemeException(scheme);
        return new UriConnectorResolver(scheme, clazz).resolve(remaining, context);
    }

    private void resolveClasspaths(String[] packages) {
        for (String pkg : packages) {
            resolvePackage(pkg);
        }
    }

    private void resolvePackage(String pkg) {
        scanForSubTypes(pkg, Connector.class, type -> registerConnectorClass((Class<? extends Connector>) type));
    }
}
