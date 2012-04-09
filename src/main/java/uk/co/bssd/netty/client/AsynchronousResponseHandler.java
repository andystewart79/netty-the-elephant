package uk.co.bssd.netty.client;

import java.io.Serializable;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class AsynchronousResponseHandler extends SimpleChannelUpstreamHandler {

	private final AsynchronousMessageCollector messageCollector;

	public AsynchronousResponseHandler(
			AsynchronousMessageCollector messageCollector) {
		this.messageCollector = messageCollector;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		Object message = e.getMessage();

		if (message instanceof Serializable) {
			this.messageCollector.onMessage((Serializable) message);
		} else {
			ctx.sendUpstream(e);
		}
	}
}