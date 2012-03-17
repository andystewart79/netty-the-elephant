package uk.co.bssd.netty.server;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import uk.co.bssd.netty.DisconnectListener;

public class DisconnectLatch implements DisconnectListener {

	private static final long DEFAULT_TIMEOUT_MS = 1000;

	private final CountDownLatch latch;
	private final long timeoutMs;

	public DisconnectLatch() {
		this(DEFAULT_TIMEOUT_MS);
	}

	public DisconnectLatch(long timeoutMs) {
		this.latch = new CountDownLatch(1);
		this.timeoutMs = timeoutMs;
	}

	@Override
	public void onDisconnect() {
		this.latch.countDown();
	}

	public boolean awaitDisconnect() {
		try {
			this.latch.await(this.timeoutMs, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new IllegalStateException("Interrupted whilst awaiting on disconnect latch", e);
		}
		return this.latch.getCount() == 0;
	}
}