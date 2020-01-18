package io.gridgo.framework.impl;

import io.reactivex.subjects.ReplaySubject;

public abstract class ReplayEventDispatcher<T> extends SubjectEventDispatcher<T> {

    public ReplayEventDispatcher() {
        super(ReplaySubject.create());
    }
}
