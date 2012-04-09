package uk.co.bssd.netty.server;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class CapturingMessageHandler<T> implements
		AsynchronousMessageHandler<T> {

	private static final long DEFAULT_TIMEOUT_MS = 1000;

	private final long timeoutMs;
	private final CountDownLatch latch;
	private final AtomicReference<T> captured;

	public CapturingMessageHandler() {
		this(DEFAULT_TIMEOUT_MS);
	}

	public CapturingMessageHandler(long timeoutMs) {
		this.timeoutMs = timeoutMs;
		this.latch = new CountDownLatch(1);
		this.captured = new AtomicReference<T>();
	}

	@Override
	public void onMessage(T message) {
		this.captured.set(message);
		this.latch.countDown();
	}

	public boolean hasCaptured() {
		return this.captured.get() != null;
	}

	public T capturedValue() {
		return this.captured.get();
	}

	public void awaitCapture() {
		try {
			this.latch.await(this.timeoutMs, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}
}