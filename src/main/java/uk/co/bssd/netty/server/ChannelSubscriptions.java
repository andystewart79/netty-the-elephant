package uk.co.bssd.netty.server;

import java.util.Collection;

import org.jboss.netty.channel.Channel;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class ChannelSubscriptions {

	private final Multimap<String, Channel> subscriptions;

	public ChannelSubscriptions() {
		this.subscriptions = HashMultimap.create();
	}

	public void subscribe(Channel channel, String channelName) {
		this.subscriptions.put(channelName, channel);
	}

	public Collection<Channel> subscribedClients(String channelName) {
		return this.subscriptions.get(channelName);
	}
}