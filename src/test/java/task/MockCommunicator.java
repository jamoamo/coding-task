/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package task;

/**
 *
 * @author James Amoore
 */
public class MockCommunicator implements Communicator
{
	private String lastMessage;
	private int lastSender;
	private int lastRecipient;
	private boolean isClosed = false;
	
	@Override
	public void receiveMessage(int senderId, int recipientId, String message)
	{
		lastMessage = message;
		lastSender = senderId;
		lastRecipient = recipientId;
	}

	@Override
	public void stopCommunication()
	{
		isClosed = true;
	}

	public String getLastMessage()
	{
		return lastMessage;
	}

	public int getLastSender()
	{
		return lastSender;
	}

	public int getLastRecipient()
	{
		return lastRecipient;
	}
	
	public boolean isClosed()
	{
		return isClosed;
	}
}
