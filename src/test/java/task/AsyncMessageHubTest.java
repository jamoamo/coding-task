package task;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AsyncMessageHubTest
{
	private final PrintStream standardOut = System.out;
	private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	
	@BeforeEach
	public void setup()
	{
		outputStream.reset();
		System.setOut(new PrintStream(outputStream));
	}
	
	@AfterEach
	public void tearDown()
	{
		System.setOut(standardOut);
	}

	@Test
	public void testRegisterPlayer()
	{
		Communicator communicator = new MockCommunicator();
		AsyncMessageHub instance = new AsyncMessageHub();
		instance.registerPlayer(1, communicator);
		
		assertTrue(instance.isPlayerRegistered(1));
	}

	@Test
	public void testSendMessage() throws Exception
	{
		int senderId = 2;
		int recipientId = 1;
		String message = "Test Message";
		AsyncMessageHub instance = new AsyncMessageHub();
		MockCommunicator communicator = new MockCommunicator();
		instance.registerPlayer(1, communicator);
		instance.sendMessage(senderId, recipientId, message);
		//wait for message to be sent asynchronously
		Thread.sleep(500);
		
		assertEquals(message, communicator.getLastMessage());
		assertEquals(recipientId, communicator.getLastRecipient());
		assertEquals(senderId, communicator.getLastSender());
	}
	
	@Test
	public void testSendMessage_notRegistered()
	{
		int senderId = 2;
		int recipientId = 1;
		String message = "Test Message";
		AsyncMessageHub instance = new AsyncMessageHub();
		instance.sendMessage(senderId, recipientId, message);
		assertEquals("Recipient 1 is not available\r\n", outputStream.toString());
	}

	@Test
	public void testIsPlayerRegistered_true()
	{
		AsyncMessageHub instance = new AsyncMessageHub();
		instance.registerPlayer(1, new MockCommunicator());
		boolean result = instance.isPlayerRegistered(1);
		assertTrue(result);
	}
	@Test
	public void testIsPlayerRegistered_false()
	{
		AsyncMessageHub instance = new AsyncMessageHub();
		instance.registerPlayer(2, new MockCommunicator());
		boolean result = instance.isPlayerRegistered(1);
		assertFalse(result);
	}
	

	@Test
	public void testStartHub()
	{
		AsyncMessageHub instance = new AsyncMessageHub();
		boolean result = instance.startHub();
		assertTrue(result);
	}

	@Test
	public void testStopHub_oneCommunicator()
	{
		AsyncMessageHub instance = new AsyncMessageHub();
		MockCommunicator communicator = new MockCommunicator();
		instance.registerPlayer(1, communicator);
		instance.stopHub();
		assertTrue(communicator.isClosed());
	}
	
	@Test
	public void testStopHub_multipleCommunicators()
	{
		AsyncMessageHub instance = new AsyncMessageHub();
		MockCommunicator communicator1 = new MockCommunicator();
		instance.registerPlayer(1, communicator1);
		MockCommunicator communicator2 = new MockCommunicator();
		instance.registerPlayer(2, communicator2);
		instance.stopHub();
		assertTrue(communicator1.isClosed());
		assertTrue(communicator2.isClosed());
	}
}
