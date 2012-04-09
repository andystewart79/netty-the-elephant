package uk.co.bssd.netty.server;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;

import uk.co.bssd.netty.dto.AsynchronousRequest;

public class AsynchronousMessageDispatcher {

	private final ExecutorService executor;
	private final AsynchronousMessageHandlers<Serializable> messageHandlers;
	
	public AsynchronousMessageDispatcher(ExecutorService executor) {
		this.executor = executor;
		this.messageHandlers = new AsynchronousMessageHandlers<Serializable>();
	}
	
	@SuppressWarnings("unchecked")
	public void registerHandler(Class<? extends Serializable> clazz,
			AsynchronousMessageHandler<? extends Serializable> handler) {
		this.messageHandlers.add(clazz,  (AsynchronousMessageHandler<Serializable>)handler);
	}
	
	public void dispatch(AsynchronousRequest request) {
		AsynchronousMessageHandler<Serializable> handler = this.messageHandlers.forType(request.payload().getClass());
		AsynchronousMessageTask task = new AsynchronousMessageTask(handler, request);
		this.executor.submit(task);
	}
}