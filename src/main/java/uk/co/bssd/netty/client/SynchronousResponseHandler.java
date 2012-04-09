package uk.co.bssd.netty.client;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import uk.co.bssd.netty.dto.SynchronousResponse;

public class SynchronousResponseHandler extends SimpleChannelUpstreamHandler {

	private final SynchronousMessageCollector messageCollector;

	public SynchronousResponseHandler(
			SynchronousMessageCollector messageCollector) {
		this.messageCollector = messageCollector;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		Object message = e.getMessage();

		if (message instanceof SynchronousResponse) {
			this.messageCollector.onMessage((SynchronousResponse) message);
		} else {
			ctx.sendUpstream(e);
		}
	}
}