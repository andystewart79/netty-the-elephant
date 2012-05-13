package uk.co.bssd.netty.client;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.serialization.ClassResolvers;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

import uk.co.bssd.netty.DisconnectEventHandler;
import uk.co.bssd.netty.DisconnectListeners;

public class ClientChannelPipelineFactory implements ChannelPipelineFactory {

	private final SynchronousResponseHandler synchronousResponseHandler;
	private final AsynchronousResponseHandler asynchronousResponseHandler;
	private final DisconnectEventHandler disconnectEventHandler;

	public ClientChannelPipelineFactory(
			SynchronousMessageCollector synchronousMessageCollector,
			AsynchronousMessageCollector asynchronousMessageCollector,
			DisconnectListeners disconnectListeners) {
		this.synchronousResponseHandler = new SynchronousResponseHandler(
				synchronousMessageCollector);
		this.asynchronousResponseHandler = new AsynchronousResponseHandler(
				asynchronousMessageCollector);
		this.disconnectEventHandler = new DisconnectEventHandler(
				disconnectListeners);
	}

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = Channels.pipeline();

		pipeline.addLast("objectDecoder",
				new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
		pipeline.addLast("objectEncoder", new ObjectEncoder());
		pipeline.addLast("synchronousResponseHandler",
				this.synchronousResponseHandler);
		pipeline.addLast("asynchronousMessageCollector",
				this.asynchronousResponseHandler);
		pipeline.addLast("disconnectEventHandler", this.disconnectEventHandler);

		return pipeline;
	}
}