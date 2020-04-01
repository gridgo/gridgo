package io.gridgo.utils.support;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class CaseInsensitiveMap<V> implements Map<String, V> {

    private @NonNull Map<String, V> delegate;

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return delegate.containsKey(key.toString().toLowerCase());
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return delegate.get(key.toString().toLowerCase());
    }

    @Override
    public V put(String key, V value) {
        return delegate.put(key.toLowerCase(), value);
    }

    @Override
    public V remove(Object key) {
        return delegate.remove(key.toString().toLowerCase());
    }

    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        Map<String, V> map = new HashMap<>();
        for (var entry : m.entrySet()) {
            map.put(entry.getKey().toLowerCase(), entry.getValue());
        }
        delegate.putAll(map);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public Set<String> keySet() {
        return delegate.keySet();
    }

    @Override
    public Collection<V> values() {
        return delegate.values();
    }

    @Override
    public Set<Entry<String, V>> entrySet() {
        return delegate.entrySet();
    }
}
