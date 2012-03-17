package uk.co.bssd.netty.server;

import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;

public class EchoServer extends BaseServer {

	public EchoServer() {
		this(new DefaultChannelGroup(EchoServer.class.getName()));
	}
	
	private EchoServer(ChannelGroup channelGroup) {
		super(channelGroup, new EchoServerChannelPipelineFactory(channelGroup));
	}
}