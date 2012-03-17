package uk.co.bssd.netty.server;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.group.ChannelGroup;

public class EchoServerChannelPipelineFactory extends DefaultServerChannelPipelineFactory {

	public EchoServerChannelPipelineFactory(ChannelGroup channelGroup) {
		super(channelGroup);
	}

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = super.getPipeline();
		pipeline.addLast("messageHandler", new EchoChannelHandler());
		return pipeline;
	}
}
