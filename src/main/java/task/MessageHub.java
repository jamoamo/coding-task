package task;

/**
 *	Interface for a message exchange hub that routes messages to the intended recipient.
 */
public interface MessageHub
{
	/**
	 * Registers a new player using the provided communicator to communicate with them.
	 *
	 * @param id the id of the player
	 * @param communicator the communicator to use to communicate with the player.
	 */
	void registerPlayer(int id, Communicator communicator);

	/**
	 * Sends a message to the intended recipient.
	 * @param senderId The id of the sender of the message.
	 * @param recipientId The id of the recipient of the message.
	 * @param message The message to send.
	 */
	void sendMessage(int senderId, int recipientId, String message);
	
}
