package uk.co.bssd.netty.server;

import java.net.SocketAddress;

public interface UnsubscribeListener {

	void onUnsubscribe(SocketAddress clientAddress, String channelName);
}