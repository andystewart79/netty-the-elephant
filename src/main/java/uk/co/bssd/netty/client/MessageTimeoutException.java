package uk.co.bssd.netty.client;

public class MessageTimeoutException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MessageTimeoutException(String message) {
		super(message);
	}
}