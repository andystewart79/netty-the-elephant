package uk.co.bssd.netty.server;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class RpcChannelHandler extends SimpleChannelUpstreamHandler {
	
	private final MessageHandlers messageHandlers;
	
	public RpcChannelHandler(MessageHandlers messageHandlers) {
		this.messageHandlers = messageHandlers;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		Object message = e.getMessage();
		MessageHandler<Object> handler = (MessageHandler<Object>)this.messageHandlers.forType(message.getClass());
		handler.onMessage(message);
	}
}