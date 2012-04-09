package uk.co.bssd.netty.server;

public interface AsynchronousMessageHandler<REQ> {

	void onMessage(REQ message);
}