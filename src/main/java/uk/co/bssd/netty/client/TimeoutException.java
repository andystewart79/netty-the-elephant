package uk.co.bssd.netty.client;

public class TimeoutException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TimeoutException(String message) {
		super(message);
	}
}