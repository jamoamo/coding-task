package task;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A message hub that opens a TCP port that remote message hubs can connect to in order to exchange messages between 
 * processes.
 */
public class ServerMessageHub extends TcpMessageHub
{
	private AtomicBoolean acceptingConnections = new AtomicBoolean(false);
	private Thread connectionAcceptThread;
	private ServerSocket serverSocket;
	private final List<SocketCommunicator> clientCommunicators = new ArrayList<>();
	
	public ServerMessageHub()
	{
	}
	
	/**
	 * Starts the hub communication by opening a Server socket and listening for connections from clients. 
	 * This method returns after the first connection has been made.
	 * @return true if a connection was successfully established.
	 */
	@Override
	public boolean startHub()
	{
		return listenForTcpConnections();
	}
	
	private boolean listenForTcpConnections()
	{
		try
		{
			this.serverSocket = new ServerSocket(10360);
			this.connectionAcceptThread = new Thread(() -> {
				while(this.acceptingConnections.get())
				{
					Socket socket;
					try
					{
						socket = serverSocket.accept();
						socket.setKeepAlive(true);
					}
					catch(IOException ioe)
					{
						if(serverSocket.isClosed())
						{
							System.out.println("Server Socket closed");
						}
						else
						{
							System.out.println("Failed to accept connection. " + ioe.getClass() + " - " + ioe.getMessage());
						}
						break;
					}

					try
					{
						SocketCommunicator communicator = new SocketCommunicator(socket, this, true);
						this.clientCommunicators.add(communicator);
					}
					catch(IOException ioe)
					{
						System.out.println("Failed to create communicator. " + ioe.getMessage());
					}
				}
				acceptingConnections.set(false);
			}, "TCP Communicator");
			acceptingConnections = new AtomicBoolean(true);
			this.connectionAcceptThread.start();
			waitForFirstConnection();
			return true;
		}
		catch(IOException ioe)
		{
			System.out.println(ioe);
			return false;
		}
	}

	/**
	 * Registers a new player using the provided communicator to communicate with them. Replicates the player 
	 * registration to client connections.
	 * 
	 * @param id the id of the player
	 * @param communicator the communicator to use to communicate with the player.
	 */
	@Override
	public void registerPlayer(int id, Communicator communicator)
	{
		if(!isPlayerRegistered(id))
		{
			clientCommunicators.forEach(c -> c.registerPlayer(id));
			super.registerPlayer(id, communicator);
		}
	}

	/**
	 * Stops the hub and closes the server socket.
	 */
	@Override
	public void stopHub()
	{
		super.stopHub();
		this.acceptingConnections.set(false);
		try
		{
			this.serverSocket.close();
		}
		catch(IOException ioe)
		{
			//ignore
		}
	}

	private void waitForFirstConnection()
	{
		while(clientCommunicators.isEmpty())
		{
			System.out.println("Waiting for connection...");
			try
			{
				Thread.sleep(2000);
			}
			catch(InterruptedException ie)
			{
				//ignore
			}
		}
	}
}
