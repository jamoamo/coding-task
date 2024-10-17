package task;

import java.io.IOException;
import java.net.Socket;

/**
 * A message hub that connects to a remote server message hub to exchange messages.
 */
public class ClientMessageHub extends TcpMessageHub
{
	private static final int SERVER_PORT = 10360;
	private static final int MAX_ATTEMPTS = 10;
	private static final long CONNECTION_ATTEMPT_WAIT_TIME_MS = 2000;
	
	private SocketCommunicator serverCommunicator;
	
	/**
	 * Starts the hub communication by opening a TCP connection to the server message hub. Tries to connect a maximum of 10 times. 
	 * Each connection attempt is 2 seconds after the previous.
	 * @return true if a connection was successfully established.
	 */
	@Override
	public boolean startHub()
	{
		return connectToServer();
	}
	
	private boolean connectToServer()
	{
		boolean connected = false;
		int attempts = 0;
		while(!connected && attempts < MAX_ATTEMPTS)
		{
			try
			{
				attempts++;
				System.out.print("Attempting to connect...");
				Socket socket = new Socket("localhost", SERVER_PORT);
				socket.setKeepAlive(true);
				System.out.println("Success");
				this.serverCommunicator = new SocketCommunicator(socket, this, false);
				connected = true;
			}
			catch(IOException ioe)
			{
				System.out.println("try again");
				try
				{
					Thread.sleep(CONNECTION_ATTEMPT_WAIT_TIME_MS);
				}
				catch(InterruptedException ie)
				{
					//ignore
				}
			}
		}
		return connected;
	}

	/**
	 * Registers a new player using the provided communicator to communicate with them.
	 * 
	 * @param id the id of the player
	 * @param communicator the communicator to use to communicate with the player.
	 */
	@Override
	public synchronized void registerPlayer(int id, Communicator communicator)
	{
		//Don't re-rerigister a player if they are already registered.
		if(!isPlayerRegistered(id))
		{
			serverCommunicator.registerPlayer(id);
			super.registerPlayer(id, communicator);
		}
	}
}
