/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package task;

import java.util.HashMap;

/**
 *
 * @author James Amoore
 */
public class MockTcpMessageHub extends TcpMessageHub
{
	private String lastMessage;
	private int lastSender;
	private int lastRecipient;
	private HashMap<Integer, Communicator> communicators = new HashMap<>();
	
	@Override
	public void registerPlayer(int id, Communicator communicator)
	{
		this.communicators.put(id, communicator);
	}

	@Override
	public void sendMessage(int senderId, int recipientId, String message)
	{
		this.lastMessage = message;
		this.lastRecipient = recipientId;
		this.lastSender = senderId;
	}
	
	public Communicator getRegisteredPlayer(int id)
	{
		return this.communicators.get(id);
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
}
