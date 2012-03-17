package uk.co.bssd.netty.server;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.handler.codec.serialization.ClassResolvers;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

import uk.co.bssd.netty.ChannelEventHandler;

public class ServerChannelPipelineFactory implements ChannelPipelineFactory {

	private final ChannelGroup channelGroup;
	
	public ServerChannelPipelineFactory(ChannelGroup channelGroup) {
		this.channelGroup = channelGroup;
	}
	
	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = Channels.pipeline();
		pipeline.addLast("objectDecoder",
				new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
		pipeline.addLast("objectEncoder", new ObjectEncoder());
		pipeline.addLast("channelEventHandler", new ChannelEventHandler(
				this.channelGroup));
		pipeline.addLast("messageHandler", new EchoChannelHandler());
		return pipeline;
	}
}