package uk.co.bssd.netty.server;

import java.io.Serializable;

import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;

public class RpcServer extends BaseServer {

	public RpcServer() {
		this(new DefaultChannelGroup(RpcServer.class.getName()));
	}
	
	private RpcServer(ChannelGroup channelGroup) {
		super(channelGroup, new RpcServerChannelPipelineFactory(channelGroup));
	}
	
	public void registerASynchronousMessageHandler(Class<? extends Serializable> messageType, AsynchronousMessageHandler<? extends Serializable> handler) {
		pipelineFactory().registerASynchronousMessageHandler(messageType, handler);
	}
	
	public void registerSynchronousMessageHandler(Class<? extends Serializable> messageType, SynchronousMessageHandler<? extends Serializable, ? extends Serializable> handler) {
		pipelineFactory().registerSynchronousMessageHandler(messageType, handler);
	}
	
	@Override
	public RpcServerChannelPipelineFactory pipelineFactory() {
		return (RpcServerChannelPipelineFactory)super.pipelineFactory();
	}
}