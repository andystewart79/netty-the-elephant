package uk.co.bssd.netty.server;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import uk.co.bssd.netty.dto.SubscribeChannelRequest;

public class SubscribeChannelRequestHandler extends SimpleChannelUpstreamHandler {

	private final ChannelSubscriptions channelSubscriptions;
	
	public SubscribeChannelRequestHandler(ChannelSubscriptions subscriptions) {
		this.channelSubscriptions = subscriptions;
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		Object message = e.getMessage();
		
		if (message instanceof SubscribeChannelRequest) {
			String channelName = ((SubscribeChannelRequest)message).channelName();
			this.channelSubscriptions.subscribe(e.getChannel(), channelName);
		} else {
			ctx.sendUpstream(e);
		}
	}
}