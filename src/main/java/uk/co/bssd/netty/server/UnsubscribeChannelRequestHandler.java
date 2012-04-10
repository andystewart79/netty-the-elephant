package uk.co.bssd.netty.server;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import uk.co.bssd.netty.dto.UnsubscribeChannelRequest;

public class UnsubscribeChannelRequestHandler extends SimpleChannelUpstreamHandler {

	private final ChannelSubscriptions channelSubscriptions;
	
	public UnsubscribeChannelRequestHandler(ChannelSubscriptions subscriptions) {
		this.channelSubscriptions = subscriptions;
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		Object message = e.getMessage();
		
		if (message instanceof UnsubscribeChannelRequest) {
			String channelName = ((UnsubscribeChannelRequest)message).channelName();
			this.channelSubscriptions.unsubscribe(e.getChannel(), channelName);
		} else {
			ctx.sendUpstream(e);
		}
	}
}