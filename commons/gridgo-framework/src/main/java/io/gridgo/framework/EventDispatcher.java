package io.gridgo.framework;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public interface EventDispatcher<T> {

    public Disposable subscribe(Consumer<T> subscriber);
    
    public void publish(T event);
}
