package nhb.net;

import java.util.concurrent.CompletableFuture;

public interface Startable {

	CompletableFuture<Void> start();
}
