package uk.co.bssd.netty.client;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.serialization.ClassResolvers;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

import uk.co.bssd.netty.DisconnectListener;

public class MessageCollectingClient {

	private final ClientBootstrap bootstrap;
	private final ClientMessageCollector messageCollector;
	private final DisconnectListener disconnectListener;

	private Channel channel;

	public MessageCollectingClient(DisconnectListener disconnectListener) {
		this.disconnectListener = disconnectListener;

		final ChannelFactory channelFactory = new NioClientSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool());

		this.messageCollector = new ClientMessageCollector();

		this.bootstrap = new ClientBootstrap(channelFactory);
		this.bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline();

				pipeline.addLast("objectDecoder", new ObjectDecoder(
						ClassResolvers.cacheDisabled(null)));
				pipeline.addLast("objectEncoder", new ObjectEncoder());

				pipeline.addLast("messageCollector",
						MessageCollectingClient.this.messageCollector);
				pipeline.addLast("channelDisconnectHandler",
						new SimpleChannelUpstreamHandler() {
							@Override
							public void channelDisconnected(
									ChannelHandlerContext ctx,
									ChannelStateEvent e) throws Exception {
								MessageCollectingClient.this.disconnectListener.onDisconnect();
							}
						});

				return pipeline;
			}
		});
	}

	public void start(String host, int port, long connectionTimeoutMillis) {
		SocketAddress address = new InetSocketAddress(host, port);
		ChannelFuture future = this.bootstrap.connect(address);

		awaitConnection(future, connectionTimeoutMillis);

		this.channel = future.getChannel();
	}

	public void sendAsync(Serializable message) {
		this.channel.write(message);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T sendSync(Serializable message,
			Class<T> responseClass, long timeoutMillis) {
		sendAsync(message);
		return (T)awaitMessage(timeoutMillis);
	}

	public Object awaitMessage(long timeoutMillis) {
		return this.messageCollector.awaitMessage(timeoutMillis);
	}

	private void awaitConnection(ChannelFuture future,
			long connectionTimeoutMillis) {
		boolean connected;

		try {
			connected = future.await(connectionTimeoutMillis);
		} catch (InterruptedException e) {
			throw new IllegalStateException(
					"Interrupted whilst waiting to connect", e);
		}

		if (!connected) {
			String message = String.format(
					"Timed out after [%d] ms waiting for connection to server",
					connectionTimeoutMillis);
			throw new IllegalStateException(message);
		}
	}

	public void stop() {
		if (this.channel != null) {
			this.channel.close();
		}
		this.bootstrap.releaseExternalResources();
	}
}