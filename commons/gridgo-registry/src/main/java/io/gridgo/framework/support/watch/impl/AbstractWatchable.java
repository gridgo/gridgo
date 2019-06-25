package io.gridgo.framework.support.watch.impl;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import io.gridgo.framework.support.watch.Disposable;
import io.gridgo.framework.support.watch.WatchEvent;
import io.gridgo.framework.support.watch.Watchable;

public abstract class AbstractWatchable implements Watchable {

    private Set<String> watchKeys = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private Map<String, Set<Consumer<WatchEvent>>> watchMap = new ConcurrentHashMap<>();

    private Set<Consumer<WatchEvent>> globalWatchers = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Override
    public Disposable watchForChange(String key, Consumer<WatchEvent> handler) {
        if (watchKeys.add(key))
            onRegisterWatch(key);
        watchMap.computeIfAbsent(key, k -> Collections.newSetFromMap(new ConcurrentHashMap<>())) //
                .add(handler);
        return new Disposable() {

            @Override
            public void dispose() {
                watchMap.get(key).remove(handler);
            }
        };
    }

    @Override
    public Disposable watchForChange(Consumer<WatchEvent> handler) {
        globalWatchers.add(handler);
        return new Disposable() {

            @Override
            public void dispose() {
                globalWatchers.remove(handler);
            }
        };
    }

    protected void fireChange(String watchKey, WatchEvent event) {
        var handlers = watchMap.get(watchKey);
        notifyChange(event, handlers);
        notifyChange(event, globalWatchers);
    }

    protected void notifyChange(WatchEvent event, Set<Consumer<WatchEvent>> handlers) {
        handlers.forEach(handler -> handler.accept(event));
    }

    protected abstract void onRegisterWatch(String key);
}
