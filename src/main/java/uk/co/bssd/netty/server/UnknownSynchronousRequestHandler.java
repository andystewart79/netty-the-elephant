package uk.co.bssd.netty.server;

import uk.co.bssd.netty.UnknownSynchronousRequestException;

public class UnknownSynchronousRequestHandler<REQ, RESP> implements SynchronousMessageHandler<REQ, RESP>{

	private static final String TEMPLATE_MESSAGE = "No handler registered for request of type [%s]";
	
	@Override
	public RESP onMessage(REQ message) {
		String errorMessage = String.format(TEMPLATE_MESSAGE, message.getClass());
		throw new UnknownSynchronousRequestException(errorMessage);
	}
}