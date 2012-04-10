package uk.co.bssd.netty.server;

import java.io.Serializable;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.group.ChannelGroup;

public class RpcServerChannelPipelineFactory extends DefaultServerChannelPipelineFactory {

	private final RpcChannelHandler rpcChannelHandler;
	private final SubscribeChannelRequestHandler subscribeChannelHandler;
	
	public RpcServerChannelPipelineFactory(ChannelGroup channelGroup, ChannelSubscriptions channelSubscriptions) {
		super(channelGroup);
		this.rpcChannelHandler = new RpcChannelHandler();
		this.subscribeChannelHandler = new SubscribeChannelRequestHandler(channelSubscriptions);
	}

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = super.getPipeline();
		pipeline.addLast("rpcHandler", this.rpcChannelHandler);
		pipeline.addLast("subscribeHandler", this.subscribeChannelHandler);
		return pipeline;
	}
	
	public void registerAsynchronousMessageHandler(Class<? extends Serializable> clazz, AsynchronousMessageHandler<? extends Serializable> handler) {
		this.rpcChannelHandler.registerAsynchronousMessageHandler(clazz, handler);
	}
	
	public void registerSynchronousMessageHandler(Class<? extends Serializable> clazz, SynchronousMessageHandler<? extends Serializable, ? extends Serializable> handler) {
		this.rpcChannelHandler.registerSynchronousMessageHandler(clazz, handler);
	}
}