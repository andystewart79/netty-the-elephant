package uk.co.bssd.netty.server;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class BaseServer {

	private final ServerBootstrap bootstrap;
	private final ChannelGroup channelGroup;

	private final ExecutorService bossExecutor;
	private final ExecutorService workerExecutor;

	private Channel serverChannel;

	public BaseServer(ChannelGroup channelGroup,
			ChannelPipelineFactory pipelineFactory) {
		this.bossExecutor = Executors.newCachedThreadPool();
		this.workerExecutor = Executors.newCachedThreadPool();

		ChannelFactory channelFactory = new NioServerSocketChannelFactory(
				this.bossExecutor, this.workerExecutor);

		this.channelGroup = channelGroup;

		this.bootstrap = new ServerBootstrap(channelFactory);
		this.bootstrap.setPipelineFactory(pipelineFactory);
	}
	
	public ChannelPipelineFactory pipelineFactory() {
		return this.bootstrap.getPipelineFactory();
	}

	public void start(String host, int port) {
		SocketAddress localAddress = new InetSocketAddress(host, port);
		this.serverChannel = this.bootstrap.bind(localAddress);
	}

	public void stop() {
		this.serverChannel.close().awaitUninterruptibly();
		closeAllChannels();
		this.bootstrap.releaseExternalResources();
	}
	
	public void broadcast(Serializable message) {
		this.channelGroup.write(message);
	}

	private void closeAllChannels() {
		// not great, but if a connection is mid flight when shutting down we can hang
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// ignore
		}
		ChannelGroupFuture future = this.channelGroup.close();
		future.awaitUninterruptibly();
	}
}