package io.gridgo.utils.helper;

import java.util.concurrent.CompletableFuture;

public interface Startable {

	boolean isStarted();

	CompletableFuture<Void> start();

	void stop();
}
