package uk.co.bssd.netty.server;

import java.net.SocketAddress;

public interface SubscribeListener {

	void onSubscribe(SocketAddress clientAddress, String channelName);
}