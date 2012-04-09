package uk.co.bssd.netty;

public class UnknownSynchronousRequestException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UnknownSynchronousRequestException(String message) {
		super(message);
	}
}