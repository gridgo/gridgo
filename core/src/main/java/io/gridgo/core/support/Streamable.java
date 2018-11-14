package io.gridgo.core.support;

import io.reactivex.ObservableSource;

public interface Streamable<T> {

	public ObservableSource<T> asObservable();
}
