package uk.co.bssd.netty;

public interface MessageFuture {

	void awaitUninterruptibly();
	
	boolean isSuccessful();
}