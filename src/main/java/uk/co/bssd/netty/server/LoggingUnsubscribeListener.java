package uk.co.bssd.netty.server;

import java.net.SocketAddress;

public class LoggingUnsubscribeListener implements UnsubscribeListener {

	@Override
	public void onUnsubscribe(SocketAddress clientAddress, String channelName) {
		String message = String.format("Client with remote address [%s] has unsubscribed to channel with name [%s]", clientAddress.toString(), channelName);
		System.out.println(message);
	}
}