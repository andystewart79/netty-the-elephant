package uk.co.bssd.netty.server;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.co.bssd.netty.client.MessageCollectingClient;
import uk.co.bssd.netty.dto.SimpleRequest;
import uk.co.bssd.netty.dto.SimpleResponse;

public class EchoServerIntegrationTest {

	private static final String HOST = "127.0.0.1";
	private static final int PORT = 6781;

	private static final long CLIENT_CONNECTION_TIMEOUT_MS = 1000;
	private static final long RECEIVE_MESSAGE_TIMEOUT_MS = 1000;

	private MessageCollectingClient client;
	private EchoServer server;

	@Before
	public void before() {
		this.server = new EchoServer();
		this.server.start(HOST, PORT);

		this.client = new MessageCollectingClient();
		this.client.start(HOST, PORT, CLIENT_CONNECTION_TIMEOUT_MS);
	}

	@After
	public void after() {
		this.client.stop();
		this.server.stop();
	}

	@Test
	public void testServerEchoesBackMessageSentFromClient() {
		String payload = "Hello";

		this.client.send(new SimpleRequest(payload));

		Object message = this.client.awaitMessage(RECEIVE_MESSAGE_TIMEOUT_MS);
		assertThat(message, is(SimpleResponse.class));

		SimpleResponse response = (SimpleResponse) message;
		assertThat(response.payload(), is(payload));
	}
}