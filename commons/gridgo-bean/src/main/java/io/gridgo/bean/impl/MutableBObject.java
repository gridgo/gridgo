package io.gridgo.bean.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.gridgo.bean.BElement;
import lombok.NonNull;

@SuppressWarnings("unchecked")
public class MutableBObject extends AbstractBObject {

    private final Map<String, BElement> holder;

    public MutableBObject(@NonNull Map<String, BElement> holder) {
        this.holder = holder;
    }

    public int size() {
        return holder.size();
    }

    public boolean isEmpty() {
        return holder.isEmpty();
    }

    public boolean containsKey(Object key) {
        return holder.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return holder.containsValue(value);
    }

    public BElement get(Object key) {
        return holder.get(key);
    }

    public BElement put(@NonNull String key, @NonNull BElement value) {
        return holder.put(key, value);
    }

    public BElement remove(Object key) {
        return holder.remove(key);
    }

    public void putAll(@NonNull Map<? extends String, ? extends BElement> m) {
        holder.putAll(m.entrySet() //
                       .stream() //
                       .filter(entry -> entry.getKey() != null && entry.getValue() != null) //
                       .collect(Collectors.toMap(Entry::getKey, Entry::getValue)));
    }

    public void clear() {
        holder.clear();
    }

    public Set<String> keySet() {
        return holder.keySet();
    }

    public Collection<BElement> values() {
        return holder.values();
    }

    public Set<Entry<String, BElement>> entrySet() {
        return holder.entrySet();
    }

    public BElement getOrDefault(Object key, BElement defaultValue) {
        return holder.getOrDefault(key, defaultValue);
    }

    public void forEach(BiConsumer<? super String, ? super BElement> action) {
        holder.forEach(action);
    }

    public void replaceAll(BiFunction<? super String, ? super BElement, ? extends BElement> function) {
        holder.replaceAll(function);
    }

    public BElement putIfAbsent(String key, @NonNull BElement value) {
        return holder.putIfAbsent(key, value);
    }

    public boolean remove(Object key, Object value) {
        return holder.remove(key, value);
    }

    public boolean replace(String key, BElement oldValue, @NonNull BElement newValue) {
        return holder.replace(key, oldValue, newValue);
    }

    public BElement replace(String key, @NonNull BElement value) {
        return holder.replace(key, value);
    }

    public BElement computeIfAbsent(String key, Function<? super String, ? extends BElement> mappingFunction) {
        return holder.computeIfAbsent(key, mappingFunction);
    }

    public BElement computeIfPresent(String key, BiFunction<? super String, ? super BElement, ? extends BElement> remappingFunction) {
        return holder.computeIfPresent(key, remappingFunction);
    }

    public BElement compute(String key, BiFunction<? super String, ? super BElement, ? extends BElement> remappingFunction) {
        return holder.compute(key, remappingFunction);
    }

    public BElement merge(String key, BElement value, BiFunction<? super BElement, ? super BElement, ? extends BElement> remappingFunction) {
        return holder.merge(key, value, remappingFunction);
    }

}
