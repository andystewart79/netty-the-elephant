package uk.co.bssd.netty.dto;

import java.io.Serializable;

public class SubscribeChannelRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String channelName;
	
	public SubscribeChannelRequest(String channelName) {
		this.channelName = channelName;
	}

	public String channelName() {
		return this.channelName;
	}
}