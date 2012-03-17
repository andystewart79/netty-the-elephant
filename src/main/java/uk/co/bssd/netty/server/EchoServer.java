package uk.co.bssd.netty.server;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class EchoServer {

	private final ServerBootstrap bootstrap;
	private final ChannelGroup serverChannelGroup;
	
	public EchoServer() {
		this.serverChannelGroup = new DefaultChannelGroup(EchoServer.class.getName());

		ChannelFactory channelFactory = new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool());
		
		ChannelPipelineFactory pipelineFactory = new ServerChannelPipelineFactory(this.serverChannelGroup);

		this.bootstrap = new ServerBootstrap(channelFactory);
		this.bootstrap.setPipelineFactory(pipelineFactory);
	}

	public void start(String host, int port) {
		SocketAddress localAddress = new InetSocketAddress(host, port);
		this.serverChannelGroup.add(this.bootstrap.bind(localAddress));
	}

	public void stop() {
		this.serverChannelGroup.close();
		this.bootstrap.releaseExternalResources();
	}
}