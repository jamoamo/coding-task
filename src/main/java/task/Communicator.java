package task;

/**
 * Interface for entities that exchange messages.
 */
public interface Communicator
{
	/**
	 * Receive a message from the message hub.
	 * @param senderId The id of the sender of the message.
	 * @param recipientId The id of the recipient of the message.
	 * @param message The message that was sent.
	 */
	void receiveMessage(int senderId, int recipientId, String message);
	
	/**
	 * Stops the communicator from communicating.
	 */
	void stopCommunication();
}
