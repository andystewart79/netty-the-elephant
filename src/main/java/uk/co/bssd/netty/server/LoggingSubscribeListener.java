package uk.co.bssd.netty.server;

import java.net.SocketAddress;

public class LoggingSubscribeListener implements SubscribeListener {

	@Override
	public void onSubscribe(SocketAddress clientAddress, String channelName) {
		String message = String.format("Client with remote address [%s] has subscribed to channel with name [%s]", clientAddress.toString(), channelName);
		System.out.println(message);
	}
}