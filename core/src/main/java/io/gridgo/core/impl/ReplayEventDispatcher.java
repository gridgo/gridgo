package io.gridgo.core.impl;

import io.gridgo.core.EventDispatcher;
import io.gridgo.framework.AbstractComponentLifecycle;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;

public abstract class ReplayEventDispatcher<T> extends AbstractComponentLifecycle implements EventDispatcher<T> {
    
    private Subject<T> subject = ReplaySubject.create();

    @Override
    public Disposable subscribe(Consumer<T> subscriber) {
        return subject.subscribe(subscriber);
    }

    @Override
    public void publish(T event) {
        subject.onNext(event);
    }
}
