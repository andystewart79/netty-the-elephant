package uk.co.bssd.netty;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class DisconnectEventHandler extends SimpleChannelUpstreamHandler {

	private final DisconnectListeners disconnectListeners;
	
	public DisconnectEventHandler(DisconnectListeners disconnectListeners) {
		this.disconnectListeners = disconnectListeners;
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx,
			ChannelStateEvent e) throws Exception {
		for (DisconnectListener listener : this.disconnectListeners) {
			try {
				listener.onDisconnect();
			} catch (RuntimeException ex) {
				// TODO log
			}
		}
	}
}