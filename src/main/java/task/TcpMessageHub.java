package task;

/**
 * Abstract class for message hubs that communicate over TCP.
 */
public abstract class TcpMessageHub extends AsyncMessageHub
{
	/**
	 * Registers a player received from a remote message hub.
	 * @param id the id of the player
	 * @param communicator the communicator to be used to communicate with the player.
	 */
	public synchronized void registerPlayerFromRemote(int id, Communicator communicator)
	{
		super.registerPlayer(id, communicator);
	}
}
