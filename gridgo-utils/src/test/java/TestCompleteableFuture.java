import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import io.gridgo.utils.ThreadUtils;

public class TestCompleteableFuture {

	public static void main(String[] args) {
		for (int i = 0; i < 10; i++) {
			CompletableFuture<Integer> future = calcSum(0, i);
			future.thenAccept(sum -> {
				System.out.println(Thread.currentThread().getName() + " ==> success: " + sum);
			}).exceptionally(ex -> {
				System.out.println(Thread.currentThread().getName() + " ==> fail: " + ex.getMessage());
				return null;
			});
		}

		ThreadUtils.sleep(1000);
		System.exit(0);
	}

	static Random rand = new Random(System.nanoTime());

	static CompletableFuture<Integer> calcSum(int a, int b) {
		// calc random before execute because random object is not thread-safe
		final boolean success = rand.nextInt(10) % 2 == 0;
		final long timeToSleep = 6l + rand.nextInt(5);

		return CompletableFuture.supplyAsync(() -> {
			// sleep represent doing something
			ThreadUtils.sleep(timeToSleep);

			// working done, return result
			if (success) {
				return a + b;
			}
			throw new RuntimeException("Random value is odd, result: " + (a + b));
		}).orTimeout(10, TimeUnit.MILLISECONDS);
	}
}
