package uk.co.bssd.netty.server;

import java.net.SocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SubscribeLatch implements SubscribeListener {

	private static final long DEFAULT_TIMEOUT_MS = 1000;

	private final CountDownLatch latch;
	private final long timeoutMs;

	public SubscribeLatch() {
		this(DEFAULT_TIMEOUT_MS);
	}

	public SubscribeLatch(long timeoutMs) {
		this.latch = new CountDownLatch(1);
		this.timeoutMs = timeoutMs;
	}

	@Override
	public void onSubscribe(SocketAddress clientAddress, String channelName) {
		this.latch.countDown();
	}

	public boolean awaitSubscriptionComplete() {
		try {
			this.latch.await(this.timeoutMs, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new IllegalStateException(
					"Interrupted whilst awaiting on subscription to complete", e);
		}
		return this.latch.getCount() == 0;
	}
}