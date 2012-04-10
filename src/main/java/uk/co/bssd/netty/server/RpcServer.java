package uk.co.bssd.netty.server;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class RpcServer {

	private final ServerBootstrap bootstrap;
	private final ChannelGroup channelGroup;

	private final ChannelSubscriptions channelSubscriptions;
	private final RpcServerChannelPipelineFactory pipelineFactory;

	private Channel serverChannel;

	public RpcServer() {
		this.channelGroup = new DefaultChannelGroup(getClass().getName());
		
		this.channelSubscriptions = new ChannelSubscriptions();
		registerSubscribeListener(new LoggingSubscribeListener());
		registerUnsubscribeListener(new LoggingUnsubscribeListener());
		
		this.pipelineFactory = new RpcServerChannelPipelineFactory(
				this.channelGroup, this.channelSubscriptions);

		this.bootstrap = new ServerBootstrap(channelFactory());
		this.bootstrap.setPipelineFactory(this.pipelineFactory);
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
	
	public void registerSubscribeListener(SubscribeListener listener) {
		this.channelSubscriptions.addSubscribeListener(listener);
	}
	
	public void registerUnsubscribeListener(UnsubscribeListener listener) {
		this.channelSubscriptions.addUnsubscribeListener(listener);
	}

	public void registerAsynchronousMessageHandler(
			Class<? extends Serializable> messageType,
			AsynchronousMessageHandler<? extends Serializable> handler) {
		this.pipelineFactory.registerAsynchronousMessageHandler(messageType,
				handler);
	}

	public void registerSynchronousMessageHandler(
			Class<? extends Serializable> messageType,
			SynchronousMessageHandler<? extends Serializable, ? extends Serializable> handler) {
		this.pipelineFactory.registerSynchronousMessageHandler(messageType,
				handler);
	}

	public void broadcast(Serializable message) {
		this.channelGroup.write(message);
	}

	public void broadcast(Serializable message, String channelName) {
		Collection<Channel> subscribedClients = this.channelSubscriptions
				.subscribedClients(channelName);

		for (Channel client : subscribedClients) {
			client.write(message);
		}
	}

	private ChannelFactory channelFactory() {
		ExecutorService bossExecutor = Executors.newCachedThreadPool();
		ExecutorService workerExecutor = Executors.newCachedThreadPool();

		ChannelFactory channelFactory = new NioServerSocketChannelFactory(
				bossExecutor, workerExecutor);
		return channelFactory;
	}

	private void closeAllChannels() {
		// not great, but if a connection is mid flight when shutting down we
		// can hang
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// ignore
		}
		ChannelGroupFuture future = this.channelGroup.close();
		future.awaitUninterruptibly();
	}
}