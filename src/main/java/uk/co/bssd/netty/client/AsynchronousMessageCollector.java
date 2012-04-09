package uk.co.bssd.netty.client;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class AsynchronousMessageCollector {

	private final BlockingQueue<Serializable> messages;
	
	public AsynchronousMessageCollector() {
		this.messages = new LinkedBlockingQueue<Serializable>();
	}
	
	public void onMessage(Serializable message) {
		this.messages.offer(message);
	}
	
	public Serializable take(long timeoutMillis) {
		try {
			return this.messages.poll(timeoutMillis, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			return null;
		}
	}
}