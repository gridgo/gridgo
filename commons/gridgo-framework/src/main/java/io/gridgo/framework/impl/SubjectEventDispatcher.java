package io.gridgo.framework.impl;

import io.gridgo.framework.EventDispatcher;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.Subject;

public abstract class SubjectEventDispatcher<T> extends AbstractComponentLifecycle implements EventDispatcher<T> {

    private Subject<T> subject;

    public SubjectEventDispatcher(Subject<T> subject) {
        this.subject = subject;
    }

    @Override
    public Disposable subscribe(Consumer<T> subscriber) {
        return subject.subscribe(subscriber);
    }

    @Override
    public void publish(T event) {
        subject.onNext(event);
    }
}
