package uk.co.bssd.netty.server;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import uk.co.bssd.netty.dto.SimpleRequest;
import uk.co.bssd.netty.dto.SimpleResponse;

public class EchoChannelHandler extends SimpleChannelUpstreamHandler {

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		SimpleRequest request = (SimpleRequest)e.getMessage();
		SimpleResponse response = new SimpleResponse(request.payload());
		e.getChannel().write(response);
	}
}