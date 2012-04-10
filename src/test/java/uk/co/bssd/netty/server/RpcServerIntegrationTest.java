package uk.co.bssd.netty.server;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.Serializable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.co.bssd.netty.UnknownSynchronousRequestException;
import uk.co.bssd.netty.client.MessageTimeoutException;
import uk.co.bssd.netty.client.RpcClient;
import uk.co.bssd.netty.dto.SimpleRequest;
import uk.co.bssd.netty.dto.SimpleResponse;

public class RpcServerIntegrationTest {

	private static final String HOST = "127.0.0.1";
	private static final int PORT = 6789;

	private static final long CLIENT_CONNECTION_TIMEOUT_MS = 1000;
	private static final long CLIENT_MESSAGE_RECEIVE_DEFAULT_TIMEOUT_MS = 1000;
	private static final long CLIENT_MESSAGE_RECEIVE_SHORT_TIMEOUT_MS = 2;

	private static final String HELLO = "hello";
	
	private static final String MESSAGE_CHANNEL = "channel";

	private SimpleRequest request;

	private DisconnectLatch clientDisconnectLatch;
	private RpcClient client;
	private RpcServer server;

	@Before
	public void before() throws Exception {
		this.request = new SimpleRequest(HELLO);

		this.server = new RpcServer();
		this.server.start(HOST, PORT);

		this.clientDisconnectLatch = new DisconnectLatch();
		this.client = new RpcClient();
		this.client.addDisconnectListener(this.clientDisconnectLatch);
		startClient();
	}

	@After
	public void after() {
		this.client.stop();
		this.server.stop();
	}

	@Test
	public void testBroadcastingMessageToAllClientsFromServerIsReceivedByClient() {
		Serializable message = HELLO;
		this.server.broadcast(message);
		assertThat(clientAwaitMessage(), is(message));
	}
	
	@Test
	public void testBroadcastingMessageOnChannelNotSubscribedToByClientIsNotReceivedByClient() {
		this.server.broadcast(HELLO, MESSAGE_CHANNEL);
		assertThat(this.client.awaitMessage(CLIENT_MESSAGE_RECEIVE_SHORT_TIMEOUT_MS), is(nullValue()));
	}
	
	@Test
	public void testBroadcastingMessageOnChannelSubscribedToByClientIsReceivedByClient() {
		this.client.subscribe(MESSAGE_CHANNEL);
		// TODO : this sleep must die, need to have a listener on the server that we can use to wait for subscriptions to happen
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
		}
		Serializable message = HELLO;
		this.server.broadcast(message, MESSAGE_CHANNEL);
		assertThat(clientAwaitMessage(), is(message));
	}

	@Test
	public void testSendingMessageAsyncToTheServerInvokesHandlerAssociatedWithTheMessageType() {
		CapturingMessageHandler<SimpleRequest> messageHandler = new CapturingMessageHandler<SimpleRequest>();
		this.server.registerAsynchronousMessageHandler(SimpleRequest.class,
				messageHandler);

		this.client.sendAsync(this.request);

		messageHandler.awaitCapture();

		assertThat(messageHandler.hasCaptured(), is(true));
		assertThat(messageHandler.capturedValue().payload(), is(HELLO));
	}

	@Test
	public void testSendingMessageSyncToTheServerInvokesHandlerAndAwaitsResponse() {
		this.server.registerSynchronousMessageHandler(SimpleRequest.class,
				new EchoSimpleRequestHandler());

		SimpleResponse response = this.client
				.sendSync(this.request, SimpleResponse.class,
						CLIENT_MESSAGE_RECEIVE_DEFAULT_TIMEOUT_MS);
		assertThat(response.payload(), is(HELLO));
	}

	@Test
	public void testSendingMessageSyncToTheServerCorrelatesCorrectResponse() {
		this.server.registerSynchronousMessageHandler(SimpleRequest.class,
				new EchoSimpleRequestHandler());

		this.server.broadcast(new SimpleResponse(
				"Not the answer you are looking for"));
		SimpleResponse response = this.client
				.sendSync(this.request, SimpleResponse.class,
						CLIENT_MESSAGE_RECEIVE_DEFAULT_TIMEOUT_MS);
		assertThat(response.payload(), is(HELLO));
	}

	@Test(expected = IllegalThreadStateException.class)
	public void testSendingMessageSyncToTheServerWhichResultsInAnExceptionIsRethrownInTheClient() {
		this.server.registerSynchronousMessageHandler(SimpleRequest.class,
				new IllegalThreadStateExceptionThrowingHandler());

		this.client.sendSync(this.request, SimpleResponse.class,
				CLIENT_MESSAGE_RECEIVE_DEFAULT_TIMEOUT_MS);
	}

	@Test
	public void testStoppingServerCausesDisconnectAtClient() {
		this.server.stop();
		assertThat(this.clientDisconnectLatch.awaitDisconnect(), is(true));
	}

	@Test
	public void testClientCanBeStoppedAndRestarted() {
		this.client.stop();
		startClient();
		this.server.broadcast(HELLO);
		assertThat(clientAwaitMessage(), is(notNullValue()));
	}

	@Test(expected = IllegalStateException.class)
	public void testStartingAnAlreadyRunningClientThrowsAnException() {
		startClient();
	}

	@Test
	public void testStoppingAnAlreadyStoppedClientHasNoEffect() {
		this.client.stop();
		this.client.stop();
	}

	@Test
	public void testClientAwaitingMessageReceivesNullIfTimeoutExpiresBeforeAMessageIsSent() {
		assertThat(
				this.client
						.awaitMessage(CLIENT_MESSAGE_RECEIVE_SHORT_TIMEOUT_MS),
				is(nullValue()));
	}

	@Test(expected = MessageTimeoutException.class)
	public void testLongRunningProcessOnServerThrowsTimeoutExceptionOnClient() {
		this.server.registerSynchronousMessageHandler(String.class,
				new SynchronousMessageHandler<Serializable, Serializable>() {
					@Override
					public Serializable onMessage(Serializable message) {
						try {
							Thread.sleep(CLIENT_MESSAGE_RECEIVE_DEFAULT_TIMEOUT_MS);
						} catch (InterruptedException e) {
						}
						return null;
					}
				});

		this.client.sendSync(HELLO, Serializable.class,
				CLIENT_MESSAGE_RECEIVE_SHORT_TIMEOUT_MS);
	}

	@Test(expected = UnknownSynchronousRequestException.class)
	public void testSendingAnUnknownSynchronousRequestTypeOnTheServerThrowsAnExceptionInTheClient() {
		this.client.sendSync(HELLO, String.class,
				CLIENT_MESSAGE_RECEIVE_DEFAULT_TIMEOUT_MS);
	}

	private void startClient() {
		this.client.start(HOST, PORT, CLIENT_CONNECTION_TIMEOUT_MS);
	}

	private Serializable clientAwaitMessage() {
		Serializable received = this.client
				.awaitMessage(CLIENT_MESSAGE_RECEIVE_DEFAULT_TIMEOUT_MS);
		return received;
	}
}