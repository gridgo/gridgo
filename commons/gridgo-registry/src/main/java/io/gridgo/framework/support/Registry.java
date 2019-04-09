package io.gridgo.framework.support;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.gridgo.framework.support.exceptions.BeanNotFoundException;
import io.gridgo.utils.PrimitiveUtils;

/**
 * Acts as a dictionary for Gridgo application. It can be used to store and
 * lookup entries by name and type.
 */
public interface Registry {

    public static final Pattern REGISTRY_SUB_PATTERN = Pattern.compile("\\$\\{([^\\{]*)\\}");

    /**
     * Lookup an entry by name
     * 
     * @param name the name of the entry
     * @return the value of the entry, or null if there is no such entry
     */
    public Object lookup(String name);

    /**
     * Lookup an entry by name and cast the return value to the specified type
     * 
     * @param name the name of the entry
     * @param type the expected type of the entry
     * @return the value of the entry
     * @throws ClassCastException if the entry has a different type
     */
    @SuppressWarnings("unchecked")
    public default <T> T lookup(String name, Class<T> type) {
        Object answer = lookup(name);
        if (answer == null)
            return null;
        if (type == String.class)
            return (T) substituteRegistriesRecursive(answer.toString());
        if (PrimitiveUtils.isPrimitive(type))
            return PrimitiveUtils.getValueFrom(type, answer);
        return type.cast(answer);
    }

    /**
     * Register new entry
     * 
     * @param name   the entry name
     * @param answer the entry value
     * @return the current Registry object
     */
    public Registry register(String name, Object answer);

    /**
     * Lookup an entry by name and throw exception if the entry is not available.
     * 
     * @param name the name of the entry
     * @return the value of the entry
     * @throws BeanNotFoundException if the entry is not available
     */
    public default Object lookupMandatory(String name) {
        var answer = lookup(name);
        if (answer == null)
            throw new BeanNotFoundException("Bean " + name + " cannot be found using " + this.getClass().getName());
        return answer;
    }

    /**
     * Lookup an entry by name and throw exception if the entry is not available.
     * Also cast the return value to the specified type
     * 
     * @param name the name of the entry
     * @param type the expected type of the entry
     * @return the value of the entry
     * @throws BeanNotFoundException if the entry is not available
     * @throws ClassCastException    if the entry has a different type
     */
    public default <T> T lookupMandatory(String name, Class<T> type) {
        var answer = lookup(name, type);
        if (answer == null)
            throw new BeanNotFoundException("Bean " + name + " cannot be found using " + this.getClass().getName());
        return answer;
    }

    /**
     * Substitute all placeholders in the specified text with the correct entry in
     * the registry.
     * 
     * @param text the text to be substituted
     * @return the substituted text
     */
    public default String substituteRegistries(String text) {
        if (text.indexOf('$') == -1)
            return text;
        var matcher = REGISTRY_SUB_PATTERN.matcher(text);
        if (!matcher.find())
            return text;
        return matcher.replaceAll(result -> {
            var obj = lookup(result.group(1));
            return obj != null ? Matcher.quoteReplacement(obj.toString()) : "";
        });
    }

    /**
     * Substitute all placeholders in the specified text with the correct entry in
     * the registry. This operation is called in a loop until no more placeholders
     * can be found.
     * 
     * @param text the text to be substituted
     * @return the substituted text
     */
    public default String substituteRegistriesRecursive(String text) {
        if (text.indexOf('$') == -1)
            return text;
        while (true) {
            var matcher = REGISTRY_SUB_PATTERN.matcher(text);
            if (!matcher.find())
                return text;
            text = matcher.replaceAll(result -> {
                var obj = lookup(result.group(1));
                return obj != null ? Matcher.quoteReplacement(obj.toString()) : "";
            });
        }
    }

    public default Object lookupByType(Class<?> type) {
        return null;
    }
}
