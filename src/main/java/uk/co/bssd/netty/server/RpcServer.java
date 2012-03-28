package uk.co.bssd.netty.server;

import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;

public class RpcServer extends BaseServer {

	private final MessageHandlers messageHandlers;
	
	public RpcServer() {
		this(new DefaultChannelGroup(RpcServer.class.getName()), new MessageHandlers());
	}
	
	private RpcServer(ChannelGroup channelGroup, MessageHandlers messageHandlers) {
		super(channelGroup, new RpcServerChannelPipelineFactory(channelGroup, messageHandlers));
		this.messageHandlers = messageHandlers;
	}
	
	public void addMessageHandler(Class<?> messageType, MessageHandler<?, ?> handler) {
		this.messageHandlers.add(messageType, handler);
	}
}