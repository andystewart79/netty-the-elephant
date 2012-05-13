package uk.co.bssd.netty;

import java.util.Arrays;
import java.util.List;

import org.jboss.netty.channel.ChannelFuture;

public class ChannelFutureAdapter implements MessageFuture {

	private final List<ChannelFuture> futures;
	
	public ChannelFutureAdapter(ChannelFuture... futures) {
		this(Arrays.asList(futures));
	}
	
	public ChannelFutureAdapter(List<ChannelFuture> futures) {
		this.futures = futures;
	}
	
	@Override
	public void awaitUninterruptibly() {
		for (ChannelFuture future : this.futures) {
			future.awaitUninterruptibly();
		}
	}

	@Override
	public boolean isSuccessful() {
		for (ChannelFuture future : this.futures) {
			if (!future.isSuccess()) {
				return false;
			}
		}
		return true;
	}
}