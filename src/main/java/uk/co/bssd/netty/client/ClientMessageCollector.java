package uk.co.bssd.netty.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class ClientMessageCollector extends SimpleChannelUpstreamHandler{

	private final BlockingQueue<Object> messages;
	
	public ClientMessageCollector() {
		this.messages = new LinkedBlockingQueue<Object>();
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		this.messages.add(e.getMessage());
	}
	
	public Object awaitMessage(long timeoutMillis) {
		Object message;
		try {
			message = messages.poll(timeoutMillis, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new IllegalStateException("Interrupted waiting for message", e);
		}
		if (message == null) {
			String error = String.format("Timed out waiting for message to be received after [%d]ms", timeoutMillis);
			throw new IllegalStateException(error);
		}
		return message;
	}
}