package io.gridgo.framework.support.watch;

import java.util.function.Consumer;

public interface Watchable {

    public Disposable watchForChange(Consumer<WatchEvent> handler);

    public Disposable watchForChange(String key, Consumer<WatchEvent> handler);
}
