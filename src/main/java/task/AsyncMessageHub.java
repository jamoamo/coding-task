package task;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A message hub that asynchronously routes messages from the sender to the intended recipient. Base implementation is used when 
 * all players are in the same process as the message hub.
 */
public class AsyncMessageHub implements MessageHub
{
	private final HashMap<Integer, Communicator> players = new HashMap<>();
	private final ExecutorService executor = Executors.newFixedThreadPool(3);

	/**
	 * Registers a new player using the provided communicator to communicate with them.
	 * 
	 * @param id the id of the player
	 * @param communicator the communicator to use to communicate with the player.
	 */
	@Override
	public synchronized void registerPlayer(int id, Communicator communicator)
	{
		this.players.put(id, communicator);
	}

	/**
	 * Sends a message to the intended recipient.
	 * @param senderId The id of the sender of the message.
	 * @param recipientId The id of the recipient of the message.
	 * @param message The message to send.
	 */
	@Override
	public synchronized void sendMessage(int senderId, int recipientId, String message)
	{
		Communicator recepientCommunicator = this.players.get(recipientId);
		if(recepientCommunicator == null)
		{
			System.out.println("Recipient " + recipientId + " is not available");
			return;
		}
		this.executor.execute(() -> recepientCommunicator.receiveMessage(senderId, recipientId, message));
	}
	
	/**
	 * Indicates if a player with the provided ID is already registered.
	 * @param id the id of the player
	 * @return true if the player is already registered, else false.
	 */
	protected boolean isPlayerRegistered(int id)
	{
		return players.containsKey(id);
	}
	
	/**
	 * Starts the hub. Base implementation does nothing.
	 * @return true
	 */
	public boolean startHub()
	{
		return true;
	}

	/**
	 * Stops the hub. Ensures that all communicators stop their communications.
	 */
	public void stopHub()
	{
		this.executor.shutdown();
		players.values().forEach(comm -> comm.stopCommunication());
	}
}
