package uk.co.bssd.netty.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.co.bssd.netty.client.RpcClient;

public class NonFunctionalIntegrationTest {

	private static final String HOST = "127.0.0.1";
	private static final int PORT = 6789;

	private static final long CLIENT_CONNECTION_TIMEOUT_MS = 1000;

	private static final int NUMBER_MESSAGES = 20000;

	private List<Integer> receivedMessages;
	private CountDownLatch messagesLatch;

	private RpcClient client;
	private RpcServer server;

	@Before
	public void before() {
		this.messagesLatch = new CountDownLatch(NUMBER_MESSAGES);
		this.receivedMessages = new ArrayList<Integer>();

		this.server = new RpcServer();

		this.server.registerAsynchronousMessageHandler(Integer.class,
				new AsynchronousMessageHandler<Integer>() {
					@Override
					public void onMessage(Integer message) {
						message(message);
					}
				});

		this.server.start(HOST, PORT);

		this.client = new RpcClient();
		this.client.start(HOST, PORT, CLIENT_CONNECTION_TIMEOUT_MS);
	}

	private void message(Integer message) {
		this.receivedMessages.add(message);
		this.messagesLatch.countDown();
	}

	@After
	public void after() {
		this.client.stop();
		this.server.stop();
	}

	@Test
	public void testOrderIsPreservedForMessagesSentFromClientToServer() throws InterruptedException {
		List<Integer> sent = new ArrayList<Integer>();
		
		for (int i = 0; i < NUMBER_MESSAGES; i++) {
			Integer message = Integer.valueOf(i);
			this.client.sendAsync(message);
			sent.add(message);
		}
		this.messagesLatch.await(10, TimeUnit.SECONDS);
		
		Assert.assertThat(sent, CoreMatchers.equalTo(this.receivedMessages));
	}
}
