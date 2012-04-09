package uk.co.bssd.netty.server;

public interface SynchronousMessageHandler<REQ, RESP> {

	RESP onMessage(REQ message);
}