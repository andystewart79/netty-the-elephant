package uk.co.bssd.netty.server;

import java.io.Serializable;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.handler.codec.serialization.ClassResolvers;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

public class RpcServerChannelPipelineFactory implements ChannelPipelineFactory {

	private final RpcChannelHandler rpcChannelHandler;
	private final ServerChannelEventHandler channelEventHandler;
	private final SubscribeChannelRequestHandler subscribeChannelHandler;
	private final UnsubscribeChannelRequestHandler unsubscribeChannelHandler;
	
	public RpcServerChannelPipelineFactory(ChannelGroup channelGroup, ChannelSubscriptions channelSubscriptions) {
		this.rpcChannelHandler = new RpcChannelHandler();
		this.channelEventHandler = new ServerChannelEventHandler(channelGroup, channelSubscriptions);
		this.subscribeChannelHandler = new SubscribeChannelRequestHandler(channelSubscriptions);
		this.unsubscribeChannelHandler = new UnsubscribeChannelRequestHandler(channelSubscriptions);
	}

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = Channels.pipeline();
		pipeline.addLast("objectDecoder",
				new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
		pipeline.addLast("objectEncoder", new ObjectEncoder());
		pipeline.addLast("channelEventHandler", this.channelEventHandler);
		pipeline.addLast("rpcHandler", this.rpcChannelHandler);
		pipeline.addLast("subscribeHandler", this.subscribeChannelHandler);
		pipeline.addLast("unsubscribeHandler", this.unsubscribeChannelHandler);
		return pipeline;
	}
	
	public void registerAsynchronousMessageHandler(Class<? extends Serializable> clazz, AsynchronousMessageHandler<? extends Serializable> handler) {
		this.rpcChannelHandler.registerAsynchronousMessageHandler(clazz, handler);
	}
	
	public void registerSynchronousMessageHandler(Class<? extends Serializable> clazz, SynchronousMessageHandler<? extends Serializable, ? extends Serializable> handler) {
		this.rpcChannelHandler.registerSynchronousMessageHandler(clazz, handler);
	}
}