package uk.co.bssd.netty.client;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import uk.co.bssd.netty.DisconnectListener;
import uk.co.bssd.netty.DisconnectListeners;
import uk.co.bssd.netty.dto.AsynchronousRequest;
import uk.co.bssd.netty.dto.SubscribeChannelRequest;
import uk.co.bssd.netty.dto.SynchronousRequest;
import uk.co.bssd.netty.dto.SynchronousResponse;
import uk.co.bssd.netty.dto.UnsubscribeChannelRequest;

public class RpcClient {

	private final SynchronousMessageCollector synchronousMessageCollector;
	private final AsynchronousMessageCollector asynchronousMessageCollector;
	private final DisconnectListeners disconnectListeners;

	private ClientBootstrap clientBootstrap;
	private Channel channel;

	public RpcClient() {
		this.synchronousMessageCollector = new SynchronousMessageCollector();
		this.asynchronousMessageCollector = new AsynchronousMessageCollector();
		this.disconnectListeners = new DisconnectListeners();
	}

	public synchronized void start(String host, int port, long connectionTimeoutMillis) {
		exceptionIfClientRunning();
		this.clientBootstrap = bootstrap();
		connect(host, port, connectionTimeoutMillis);
	}

	public synchronized void stop() {
		closeChannel();
		shutdownBootstrap();
	}
	
	public void addDisconnectListener(DisconnectListener listener) {
		this.disconnectListeners.addDisconnectListener(listener);
	}
	
	public void sendAsync(Serializable message) {
		AsynchronousRequest request = new AsynchronousRequest(message);
		send(request);
	}

	@SuppressWarnings("unchecked")
	public <T> T sendSync(Serializable message, Class<T> responseClass,
			long timeoutMillis) {
		SynchronousRequest request = new SynchronousRequest(message);
		send(request);
		SynchronousResponse response = this.synchronousMessageCollector
				.awaitResponse(request.correlationId(), timeoutMillis);

		if (response.success()) {
			return (T) response.payload();
		}

		throw (RuntimeException) response.payload();
	}
	
	public Serializable awaitMessage(long timeoutMillis) {
		return this.asynchronousMessageCollector.take(timeoutMillis);
	}

	public void subscribe(String channelName) {
		SubscribeChannelRequest subscribeRequest = new SubscribeChannelRequest(channelName);
		send(subscribeRequest);
	}
	
	public void unsubscribe(String channelName) {
		UnsubscribeChannelRequest unsubscribeRequest = new UnsubscribeChannelRequest(channelName);
		send(unsubscribeRequest);
	}
	
	private void send(Serializable request) {
		this.channel.write(request);
	}

	private ClientBootstrap bootstrap() {
		ChannelFactory channelFactory = channelFactory();
		ChannelPipelineFactory clientChannelPipelineFactory = new ClientChannelPipelineFactory(this.synchronousMessageCollector, this.asynchronousMessageCollector, this.disconnectListeners);
		
		ClientBootstrap bootstrap = new ClientBootstrap(channelFactory);
		bootstrap.setPipelineFactory(clientChannelPipelineFactory);
		
		return bootstrap;
	}
	
	private ChannelFactory channelFactory() {
		return new NioClientSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool());
	}
	
	private void exceptionIfClientRunning() {
		if (this.clientBootstrap != null) {
			throw new IllegalStateException("Client is already running");
		}
	}
	
	private void connect(String host, int port, long connectionTimeoutMillis) {
		SocketAddress address = new InetSocketAddress(host, port);
		ChannelFuture future = this.clientBootstrap.connect(address);
		awaitConnection(future, connectionTimeoutMillis);
		this.channel = future.getChannel();
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

	private void closeChannel() {
		if (this.channel != null) {
			this.channel.close();
			this.channel = null;
		}
	}
	
	private void shutdownBootstrap() {
		if (this.clientBootstrap != null) {
			this.clientBootstrap.releaseExternalResources();
			this.clientBootstrap = null;
		}
	}
}