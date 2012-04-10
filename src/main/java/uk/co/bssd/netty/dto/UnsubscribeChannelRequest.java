package uk.co.bssd.netty.dto;

import java.io.Serializable;

public class UnsubscribeChannelRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String channelName;
	
	public UnsubscribeChannelRequest(String channelName) {
		this.channelName = channelName;
	}

	public String channelName() {
		return this.channelName;
	}
}