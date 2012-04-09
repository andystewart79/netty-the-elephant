package uk.co.bssd.netty.server;

import uk.co.bssd.netty.dto.SimpleRequest;
import uk.co.bssd.netty.dto.SimpleResponse;

public class EchoSimpleRequestHandler implements SynchronousMessageHandler<SimpleRequest, SimpleResponse>{

	@Override
	public SimpleResponse onMessage(SimpleRequest request) {
		return new SimpleResponse(request.payload());
	}
}