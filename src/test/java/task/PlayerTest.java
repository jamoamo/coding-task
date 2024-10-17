/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package task;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author James Amoore
 */
public class PlayerTest
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
	public void testSetMessageHub()
	{
		MockMessageHub hub = new MockMessageHub();
		Player instance = new Player(1);
		instance.setMessageHub(hub);
		assertSame(instance, hub.getRegisteredPlayer(1));
	}

	@Test
	public void testSendMessage()
	{
		int recipient = 2;
		String messageToSend = "Message";
		Player instance = new Player(1);
		MockMessageHub hub = new MockMessageHub();
		instance.setMessageHub(hub);
		instance.sendMessage(recipient, messageToSend);
		assertEquals(1, hub.getLastSender());
		assertEquals(2, hub.getLastRecipient());
		assertEquals(messageToSend, hub.getLastMessage());
		assertEquals("Player 1 sending message to 2: Message\r\n", outputStream.toString());
	}
	
	@Test
	public void testReceiveMessage()
	{
		String message = "Message received";
		Player instance = new Player(2);
		MockMessageHub hub = new MockMessageHub();
		instance.setMessageHub(hub);
		//simulate sending to increment sent messages count
		instance.sendMessage(2, message);
		instance.receiveMessage(1, 2, message);
		assertEquals("Message received 1", hub.getLastMessage());
		assertEquals(
			 "Player 2 sending message to 2: Message received\r\n" +
			"Player 2 received message from 1: Message received\r\n" +
			"Player 2 sending message to 1: Message received 1\r\n", 
			 outputStream.toString());
		outputStream.reset();
	}
}
