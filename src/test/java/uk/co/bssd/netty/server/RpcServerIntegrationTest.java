package uk.co.bssd.netty.server;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.co.bssd.netty.client.MessageCollectingClient;
import uk.co.bssd.netty.dto.SimpleRequest;
import uk.co.bssd.netty.dto.SimpleResponse;

public class RpcServerIntegrationTest {

	private static final String HOST = "127.0.0.1";
	private static final int PORT = 6789;

	private static final long CLIENT_CONNECTION_TIMEOUT_MS = 1000;
	private static final long CLIENT_MESSAGE_RECEIVE_TIMEOUT_MS = 1000;
	
	private static final String PAYLOAD = "hello";

	private SimpleRequest request;
	
	private DisconnectLatch clientDisconnectLatch;
	private MessageCollectingClient client;
	private RpcServer server;

	@Before
	public void before() throws Exception{
		this.request = new SimpleRequest(PAYLOAD);
		
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
	public void testSendingMessageAsyncToTheServerInvokesHandlerAssociatedWithTheMessageType() {
		CapturingMessageHandler<SimpleRequest> messageHandler = new CapturingMessageHandler<SimpleRequest>();
		this.server.addMessageHandler(SimpleRequest.class, messageHandler);
		
		this.client.sendAsync(this.request);

		messageHandler.awaitCapture();
		
		assertThat(messageHandler.hasCaptured(), is(true));
		assertThat(messageHandler.capturedValue().payload(), is(PAYLOAD));
	}
	
	@Test
	public void testSendingMessageSyncToTheServerInvokesHandlerAndAwaitsResponse() {
		this.server.addMessageHandler(SimpleRequest.class, new MessageHandler<SimpleResponse, SimpleRequest>() {
			@Override
			public SimpleResponse onMessage(SimpleRequest message) {
				return new SimpleResponse(message.payload());
			}
		});
		
		SimpleResponse response = this.client.sendSync(this.request, SimpleResponse.class, CLIENT_MESSAGE_RECEIVE_TIMEOUT_MS);
		assertThat(response.payload(), is(PAYLOAD));
	}
	
	@Test
	public void testStoppingServerCausesDisconnectAtClient() {
		this.server.stop();
		assertThat(this.clientDisconnectLatch.awaitDisconnect(), is(true));
	}
}