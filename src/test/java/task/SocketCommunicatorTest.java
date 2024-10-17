/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package task;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author James Amoore
 */
public class SocketCommunicatorTest
{
	
	public SocketCommunicatorTest()
	{
	}

	@Test
	public void testRegisterPlayer() throws IOException
	{
		ByteArrayOutputStream socketOutputStream = new ByteArrayOutputStream();
		InputStream socketInputStream = new ByteArrayInputStream("register|1".getBytes());
		Socket socket = Mockito.mock(Socket.class);
		Mockito.when(socket.getOutputStream()).thenReturn(socketOutputStream);
		Mockito.when(socket.getInputStream()).thenReturn(socketInputStream);
		MockTcpMessageHub hub = new MockTcpMessageHub();
		SocketCommunicator instance = new SocketCommunicator(socket, hub, false);
		instance.registerPlayer(2);
		assertEquals("register|2\r\n", socketOutputStream.toString());
	}

	@Test
	public void testReceiveMessage() throws IOException
	{
		ByteArrayOutputStream socketOutputStream = new ByteArrayOutputStream();
		InputStream socketInputStream = new ByteArrayInputStream("register|1".getBytes());
		Socket socket = Mockito.mock(Socket.class);
		Mockito.when(socket.getOutputStream()).thenReturn(socketOutputStream);
		Mockito.when(socket.getInputStream()).thenReturn(socketInputStream);
		MockTcpMessageHub hub = new MockTcpMessageHub();
		SocketCommunicator instance = new SocketCommunicator(socket, hub, false);
		instance.receiveMessage(2, 1, "Message to send");
		assertEquals("receive|2|1|Message to send\r\n", socketOutputStream.toString());
	}

	@Test
	public void testStopCommunication_client() throws IOException
	{
		ByteArrayOutputStream socketOutputStream = new ByteArrayOutputStream();
		InputStream socketInputStream = new ByteArrayInputStream("register|1".getBytes());
		Socket socket = Mockito.mock(Socket.class);
		Mockito.when(socket.getOutputStream()).thenReturn(socketOutputStream);
		Mockito.when(socket.getInputStream()).thenReturn(socketInputStream);
		Mockito.when(socket.isClosed()).thenReturn(false);
		MockTcpMessageHub hub = new MockTcpMessageHub();
		SocketCommunicator instance = new SocketCommunicator(socket, hub, false);
		instance.stopCommunication();
		
		Mockito.verify(socket).close();
	}
	
	@Test
	public void testStopCommunication_server() throws IOException
	{
		ByteArrayOutputStream socketOutputStream = new ByteArrayOutputStream();
		InputStream socketInputStream = new ByteArrayInputStream("register|1".getBytes());
		Socket socket = Mockito.mock(Socket.class);
		Mockito.when(socket.getOutputStream()).thenReturn(socketOutputStream);
		Mockito.when(socket.getInputStream()).thenReturn(socketInputStream);
		Mockito.when(socket.isClosed()).thenReturn(false);
		MockTcpMessageHub hub = new MockTcpMessageHub();
		SocketCommunicator instance = new SocketCommunicator(socket, hub, true);
		instance.stopCommunication();
		
		Mockito.verify(socket).close();
		assertEquals("shutdown\r\n", socketOutputStream.toString());
	}
	
}
