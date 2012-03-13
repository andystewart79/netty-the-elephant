package uk.co.bssd.netty.server;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.serialization.ClassResolvers;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

public class EchoServer {

	private final ServerBootstrap bootstrap;

	public EchoServer() {
		ChannelFactory channelFactory = new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool());
		
		this.bootstrap = new ServerBootstrap(channelFactory);
		this.bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("objectDecoder", new ObjectDecoder(
						ClassResolvers.cacheDisabled(null)));
				pipeline.addLast("objectEncoder", new ObjectEncoder());
				pipeline.addLast("messageHandler", new EchoChannelHandler());
				return pipeline;
			}
		});
	}

	public void start(String host, int port) {
		SocketAddress localAddress = new InetSocketAddress(host, port);
		this.bootstrap.bind(localAddress);
	}
	
	public void stop() {
		this.bootstrap.releaseExternalResources();
	}
}