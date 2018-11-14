package io.gridgo.core.support;

import io.reactivex.Observable;

public interface Streamable<T> {

	public Observable<T> asObservable();
}
