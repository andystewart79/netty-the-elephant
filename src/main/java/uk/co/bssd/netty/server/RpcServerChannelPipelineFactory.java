package uk.co.bssd.netty.server;

import java.io.Serializable;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.group.ChannelGroup;

public class RpcServerChannelPipelineFactory extends DefaultServerChannelPipelineFactory {

	private final RpcChannelHandler rpcChannelHandler;
	
	public RpcServerChannelPipelineFactory(ChannelGroup channelGroup) {
		super(channelGroup);
		this.rpcChannelHandler = new RpcChannelHandler();
	}

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = super.getPipeline();
		pipeline.addLast("rpcHandler", this.rpcChannelHandler);
		return pipeline;
	}
	
	public void registerASynchronousMessageHandler(Class<? extends Serializable> clazz, AsynchronousMessageHandler<? extends Serializable> handler) {
		this.rpcChannelHandler.registerASynchronousMessageHandler(clazz, handler);
	}
	
	public void registerSynchronousMessageHandler(Class<? extends Serializable> clazz, SynchronousMessageHandler<? extends Serializable, ? extends Serializable> handler) {
		this.rpcChannelHandler.registerSynchronousMessageHandler(clazz, handler);
	}
}