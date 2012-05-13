package uk.co.bssd.netty.server;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jboss.netty.channel.Channel;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class ChannelSubscriptions {

	private final Multimap<String, Channel> subscriptionsByChannelName;
	private final Multimap<Channel, String> subscriptionsByChannel;

	private final Set<SubscribeListener> subscribeListeners;
	private final Set<UnsubscribeListener> unsubscribeListeners;
	
	public ChannelSubscriptions() {
		this.subscriptionsByChannelName = HashMultimap.create();
		this.subscriptionsByChannel = HashMultimap.create();
		this.subscribeListeners = new HashSet<SubscribeListener>();
		this.unsubscribeListeners = new HashSet<UnsubscribeListener>();
	}

	public void subscribe(Channel channel, String channelName) {
		this.subscriptionsByChannelName.put(channelName, channel);
		this.subscriptionsByChannel.put(channel, channelName);
		notifySubscribeListeners(channel, channelName);
	}
	
	public void unsubscribe(Channel channel, String channelName) {
		this.subscriptionsByChannelName.remove(channelName, channel);
		this.subscriptionsByChannel.remove(channel, channelName);
		notifyUnsubscribeListeners(channel, channelName);
	}

	public void unsubscribeAll(Channel channel) {
		Collection<String> channelNames = new HashSet<String>(this.subscriptionsByChannel.get(channel));
		for (String channelName : channelNames) {
			unsubscribe(channel, channelName);
		}
	}
	
	public Collection<Channel> subscribedClients(String channelName) {
		return this.subscriptionsByChannelName.get(channelName);
	}
	
	public void addSubscribeListener(SubscribeListener listener) {
		this.subscribeListeners.add(listener);
	}
	
	public void addUnsubscribeListener(UnsubscribeListener listener) {
		this.unsubscribeListeners.add(listener);
	}
	
	private void notifySubscribeListeners(Channel channel, String channelName) {
		for (SubscribeListener listener : this.subscribeListeners) {
			try {
				listener.onSubscribe(channel.getRemoteAddress(), channelName);
			} catch (RuntimeException e) {
				// TODO : log exception
			}
		}
	}

	private void notifyUnsubscribeListeners(Channel channel, String channelName) {
		for (UnsubscribeListener listener : this.unsubscribeListeners) {
			try {
				listener.onUnsubscribe(channel.getRemoteAddress(), channelName);
			} catch (RuntimeException e) {
				// TODO : log exception
			}
		}
	}
}