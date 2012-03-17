package uk.co.bssd.netty.server;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.group.ChannelGroup;

public class RpcServerChannelPipelineFactory extends DefaultServerChannelPipelineFactory {

	private final MessageHandlers messageHandlers;
	
	public RpcServerChannelPipelineFactory(ChannelGroup channelGroup, MessageHandlers messageHandlers) {
		super(channelGroup);
		this.messageHandlers = messageHandlers;
	}

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = super.getPipeline();
		pipeline.addLast("rpcHandler", new RpcChannelHandler(this.messageHandlers));
		return pipeline;
	}
}