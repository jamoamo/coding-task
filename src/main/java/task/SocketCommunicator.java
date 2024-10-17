package task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Communicator that exchanges commands and messages via a socket connection.
 */
public class SocketCommunicator
	 implements Communicator
{
	private final PrintWriter outputWriter;
	private final TcpMessageHub hub;
	private final Socket socket;
	private Thread messageListenerThread;
	private final AtomicBoolean communicating = new AtomicBoolean(false);
	private final ConcurrentHashMap<Integer, Object> registeringPlayers = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, Object> sentMessages = new ConcurrentHashMap<>();
	private final boolean isServer;

	/**
	 * Constructor.
	 * @param socket the socket the communicator uses to communicate with.
	 * @param hub the hub the communicate should send messages to.
	 * @throws IOException if a network related error occurs.
	 */
	public SocketCommunicator(Socket socket, TcpMessageHub hub, boolean isServer)
		 throws IOException
	{
		this.socket = socket;
		this.outputWriter = new PrintWriter(socket.getOutputStream());
		this.hub = hub;
		this.isServer = isServer;
		listenForMessages(socket.getInputStream());
	}

	private void listenForMessages(InputStream inputStream)
	{
		BufferedReader dataReader = new BufferedReader(new InputStreamReader(inputStream));
		communicating.set(true);
		messageListenerThread = new Thread(() -> 
		{
			while(communicating.get() && !socket.isClosed() && socket.isConnected())
			{
				try
				{
					String message = dataReader.readLine();
					if(message == null)
					{
						System.out.println("Connection closed.");
						break;
					}
					handleMessage(message);
				}
				catch(IOException ex)
				{
					if(socket.isClosed())
					{
						System.out.println("Socket closed.");
					}
					else
					{
						System.out.println("ERROR: " + ex.getClass() + " " + ex.getMessage());
					}
					communicating.set(false);
				}
			}
			communicating.set(false);
		});
		messageListenerThread.start();
	}

	private void handleMessage(String message)
		 throws NumberFormatException
	{
		String[] messageParts = message.split("\\|");
		String messageType = messageParts[0];
		switch(messageType)
		{
			case "register" ->
			{
				if(messageParts.length < 2)
				{
					System.out.println("Register message does not have enough parameters.");
				}
				int playerId = Integer.parseInt(messageParts[1]);
				this.hub.registerPlayerFromRemote(playerId, this);
				sendMessageOnSocket("registered", new String[]{messageParts[1]});
			}
			case "registered" ->
			{
				if(messageParts.length < 2)
				{
					System.out.println("Register message does not have enough parameters.");
				}
				int playerId = Integer.parseInt(messageParts[1]);
				this.playerRegistered(playerId);
			}
			case "receive" ->
			{
				String senderIdStr = messageParts[1];
				String recipientIdStr = messageParts[2];
				String messageToSend = messageParts[3];
				this.hub
					 .sendMessage(Integer.parseInt(senderIdStr), Integer.parseInt(recipientIdStr), messageToSend);
			}
			case "shutdown" ->
			{
				this.hub.stopHub();
			}
			default ->
			{
				System.out.println("ERROR: Unrecognised message type: " + messageType);
			}
		}
	}

	/**
	 * Sends a player registration message over the socket connection. method is synchronous and completes when the partner 
	 * sends a response.
	 * @param id the id of the player to register.
	 */
	public void registerPlayer(int id)
	{
		Object playerMutex = new Object();

		registeringPlayers.put(id, playerMutex);
		synchronized(playerMutex)
		{
			sendMessageOnSocket("register", new String[]{Integer.toString(id)});
			try
			{
				playerMutex.wait(5000);
			}
			catch(InterruptedException ie)
			{
				//ignore
			}
		}
	}

	/**
	 * Receive a message from the message hub. Sends the received message over the socket connection. This method is 
	 * synchronous and completes after receiving a response from the partner.
	 * @param senderId The id of the sender of the message.
	 * @param recipientId The id of the recipient of the message.
	 * @param message The message that was sent.
	 */
	@Override
	public void receiveMessage(int senderId, int recipientId, String message)
	{
		Object messageMutex = new Object();

		sentMessages.put(senderId + "-" + recipientId + "-" + message, messageMutex);
		sendMessageOnSocket("receive", new String[]{Integer.toString(senderId), Integer.toString(recipientId), message});
		System.out.println(String.format("DEBUG: ending communicator received message from %d to %d", senderId, recipientId));
		
	}

	/**
	 * Stops the communications and closes the socket.
	 */
	@Override
	public void stopCommunication()
	{
		try
		{
			if(this.isServer)
			{
				sendMessageOnSocket("shutdown", new String[]{});
			}
			communicating.set(false);
			if(!this.socket.isClosed())
			{
				this.socket.close();
			}
		}
		catch(IOException ioe)
		{
			System.out.println("An error occurred closing socket: " + ioe.getMessage());
		}
	}

	private void playerRegistered(int playerId)
	{
		Object mutex = this.registeringPlayers.get(playerId);
		if(mutex != null)
		{
			synchronized(mutex)
			{
				mutex.notify();
			}
		}
	}
	
	private void sendMessageOnSocket(String type, String[] params)
	{
		StringBuilder messageBuilder = new StringBuilder(type);
		for(String p : params)
		{
			messageBuilder.append("|");
			messageBuilder.append(p);
		}
		String message = messageBuilder.toString();
		this.outputWriter.println(message);
		this.outputWriter.flush();
	}
}
