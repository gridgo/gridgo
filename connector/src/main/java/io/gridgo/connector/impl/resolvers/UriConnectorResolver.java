package io.gridgo.connector.impl.resolvers;

import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import io.gridgo.connector.Connector;
import io.gridgo.connector.ConnectorResolver;
import io.gridgo.connector.support.annotations.ConnectorEndpoint;
import io.gridgo.connector.support.config.ConnectorConfig;
import io.gridgo.connector.support.config.ConnectorContext;
import io.gridgo.connector.support.config.impl.DefaultConnectorConfig;
import io.gridgo.connector.support.exceptions.ConnectorResolutionException;
import io.gridgo.connector.support.exceptions.MalformedEndpointException;
import io.gridgo.framework.support.Registry;

public class UriConnectorResolver implements ConnectorResolver {

    private static final int MAX_PLACEHOLDER_NAME = 1024;

    private CharBuffer buffer = CharBuffer.allocate(MAX_PLACEHOLDER_NAME);

    private final Class<? extends Connector> clazz;

    private final String syntax;

    private final String scheme;

    private final boolean raw;

    private final String category;

    public UriConnectorResolver(String scheme, Class<? extends Connector> clazz) {
        this.scheme = scheme;
        this.clazz = clazz;
        var annotation = clazz.getAnnotation(ConnectorEndpoint.class);
        if (annotation != null) {
            this.syntax = annotation.syntax();
            this.raw = annotation.raw();
            this.category = annotation.category();
        } else {
            this.syntax = null;
            this.raw = false;
            this.category = null;
        }
    }

    private Map<String, Object> extractParameters(String queryPath) {
        if (queryPath == null)
            return Collections.emptyMap();
        var params = new HashMap<String, Object>();
        var queries = queryPath.split("&");
        for (String query : queries) {
            var keyValuePair = query.split("=");
            if (keyValuePair.length == 2)
                params.put(keyValuePair[0], URLDecoder.decode(keyValuePair[1], Charset.forName("utf-8")));
        }

        return params;
    }

    private String extractPlaceholderKey(String syntax, int j, CharBuffer buffer) {
        buffer.clear();
        char c;
        while (j < syntax.length() && (c = syntax.charAt(j++)) != '}') {
            buffer.put(c);
        }
        buffer.flip();
        return buffer.toString();
    }

    private Properties extractPlaceholders(String schemePart) {
        if (raw)
            return new Properties();
        return extractPlaceholders(schemePart, syntax);
    }

    protected Properties extractPlaceholders(String schemePart, String syntax) {
        var props = new Properties();
        if (syntax == null)
            return props;

        int i = 0, j = 0;
        boolean optional = false;
        int optionalIndex = -1;
        var optionalPlaceholder = new HashMap<String, String>();
        while (j < syntax.length()) {
            char syntaxChar = syntax.charAt(j);
            if (syntaxChar == '[') {
                optional = true;
                optionalIndex = i;
                j++;
                optionalPlaceholder.clear();
            } else if (syntaxChar == ']') {
                optional = false;
                optionalIndex = -1;
                j++;
                props.putAll(optionalPlaceholder);
                optionalPlaceholder.clear();
            } else if (syntaxChar == '{') {
                String placeholderName = extractPlaceholderKey(syntax, j + 1, buffer);
                String placeholderValue = extractPlaceholderValue(schemePart, i, buffer);
                j += placeholderName.length() + 2;
                i += placeholderValue.length();
                if (!placeholderValue.isEmpty() && placeholderValue.charAt(0) == '[')
                    placeholderValue = placeholderValue.substring(1, placeholderValue.length() - 1);
                if (!placeholderValue.isEmpty()) {
                    if (optional)
                        optionalPlaceholder.put(placeholderName, placeholderValue);
                    else
                        props.put(placeholderName, placeholderValue);
                }
            } else if (i < schemePart.length()) {
                char schemeChar = schemePart.charAt(i);
                if (syntaxChar != schemeChar) {
                    if (optional) {
                        i = optionalIndex;
                        j = skipOptionalPart(syntax, j);
                        optionalIndex = -1;
                        optional = false;
                        continue;
                    }
                    throw new MalformedEndpointException(
                            String.format("Malformed endpoint, invalid token at %d, expected '%c', actual '%c': %s", i,
                                    syntaxChar, schemeChar, schemePart));
                }
                i++;
                j++;
            } else {
                if (!optional)
                    break;
                i = optionalIndex;
                j = skipOptionalPart(syntax, j);
                optionalIndex = -1;
                optional = false;
            }
        }

        if (optional) {
            if (syntax.charAt(j) == ']') {
                props.putAll(optionalPlaceholder);
                j++;
            } else {
                j = skipOptionalPart(syntax, j);
            }
        }

        while (j < syntax.length() && syntax.charAt(j) == '[') {
            j = skipOptionalPart(syntax, j);
        }

        if (i < schemePart.length()) {
            throw new MalformedEndpointException(String.format("Malformed endpoint, unexpected tokens \"%s\": %s",
                    schemePart.substring(i), schemePart));
        }
        if (j < syntax.length()) {
            throw new MalformedEndpointException(String.format(
                    "Malformed endpoint, missing values for syntax \"%s\": %s", syntax.substring(j), schemePart));
        }

        return props;
    }

    private String extractPlaceholderValue(String schemePart, int i, CharBuffer buffer) {
        buffer.clear();
        char c;

        if (i >= schemePart.length())
            return "";

        boolean insideBracket = schemePart.charAt(i) == '[';
        if (insideBracket) {
            buffer.put('[');
            i++;
        }
        while (i < schemePart.length() && isPlaceholder(c = schemePart.charAt(i), insideBracket)) {
            buffer.put(c);
            i++;
        }
        if (insideBracket) {
            if (schemePart.charAt(i) != ']') {
                throw new MalformedEndpointException(
                        String.format("Malformed endpoint, invalid token at %d, expected ']', actualy '%c': %s", i,
                                schemePart.charAt(i), schemePart));
            }
            buffer.put(']');
        }

        buffer.flip();
        return buffer.toString();
    }

    private boolean isPlaceholder(char c, boolean insideBracket) {
        if (insideBracket && (c == ':' || c == '/'))
            return true;
        return c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '_' || c == '-' || c == '.'
                || c == '*' || c == ',';
    }

    @Override
    public Connector resolve(String endpoint, ConnectorContext context) {
        try {
            var config = resolveConfig(endpoint, context.getRegistry());
            return clazz.getConstructor().newInstance().initialize(config, context);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            throw new ConnectorResolutionException("Exception caught while resolving endpoint " + endpoint, e);
        }
    }

    private ConnectorConfig resolveConfig(String endpoint, Registry registry) {
        String schemePart = endpoint;
        String queryPart = null;

        if (registry != null) {
            endpoint = registry.substituteRegistries(endpoint);
        }

        int queryPartIdx = endpoint.indexOf('?');
        if (queryPartIdx != -1) {
            queryPart = endpoint.substring(queryPartIdx + 1);
            schemePart = endpoint.substring(0, queryPartIdx);
        }

        var params = extractParameters(queryPart);
        var placeholders = extractPlaceholders(schemePart);
        return DefaultConnectorConfig.builder() //
                                     .scheme(scheme) //
                                     .connectorCategory(category) //
                                     .nonQueryEndpoint(scheme + ":" + schemePart) //
                                     .originalEndpoint(endpoint) //
                                     .remaining(schemePart) //
                                     .parameters(params) //
                                     .placeholders(placeholders) //
                                     .build();
    }

    private int skipOptionalPart(String syntax, int j) {
        while (j < syntax.length() && syntax.charAt(j) != ']')
            j++;
        j++;
        return j;
    }
}
