package uk.co.bssd.netty.server;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.co.bssd.netty.client.MessageCollectingClient;
import uk.co.bssd.netty.dto.SimpleRequest;

public class RpcServerIntegrationTest {

	private static final String HOST = "127.0.0.1";
	private static final int PORT = 6781;

	private static final long CLIENT_CONNECTION_TIMEOUT_MS = 1000;

	private DisconnectLatch clientDisconnectLatch;
	private MessageCollectingClient client;
	private RpcServer server;

	@Before
	public void before() {
		this.server = new RpcServer();
		this.server.start(HOST, PORT);

		this.clientDisconnectLatch = new DisconnectLatch();
		this.client = new MessageCollectingClient(this.clientDisconnectLatch);
		this.client.start(HOST, PORT, CLIENT_CONNECTION_TIMEOUT_MS);
	}

	@After
	public void after() {
		this.client.stop();
		this.server.stop();
	}

	@Test
	public void testSendingMessageToTheServerInvokesHandlerAssociatedWithTheMessageType() {
		String payload = "hello";
		SimpleRequest request = new SimpleRequest(payload);
		
		CapturingMessageHandler<SimpleRequest> messageHandler = new CapturingMessageHandler<SimpleRequest>();
		this.server.addMessageHandler(SimpleRequest.class, messageHandler);
		
		this.client.send(request);

		messageHandler.awaitCapture();
		
		assertThat(messageHandler.hasCaptured(), is(true));
		assertThat(messageHandler.capturedValue().payload(), is(payload));
	}
}