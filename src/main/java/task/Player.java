package task;

/**
 * A player that can exchange messages with other players.
 */
public class Player implements Communicator
{
	private static final int MAX_MESSAGES = 10;
		 
	private final int playerId;
	private int messagesSent = 0;
	private MessageHub hub;
	private Runnable onMessageExchangeComplete;
	
	/**
	 * Constructor.
	 * @param playerId the id of this player.
	 */
	public Player(int playerId)
	{
		this.playerId = playerId;
	}
	
	/**
	 * Sets the message hub the player should send messages through.
	 * @param hub the message hub to use.
	 */
	public final void setMessageHub(MessageHub hub)
	{
		this.hub = hub;
		this.hub.registerPlayer(playerId, this);
	}
	
	public void initiate(Runnable onComplete)
	{
		onMessageExchangeComplete = onComplete;
		sendMessage(2, "This is the message content");
	}
	
	/**
	 * Instructs the player to send a message to the provided recipient.
	 * @param recipient The recipient that should receive the message.
	 * @param messageToSend The message that the player should send.
	 */
	public void sendMessage(int recipient, String messageToSend)
	{
		System.out.println(String.format("Player %d sending message to %d: %s", this.playerId, recipient, messageToSend));
		this.messagesSent++;
		this.hub.sendMessage(playerId, recipient, messageToSend);
	}
	
	/**
	 * Receive a message from the message hub. Sends a response to the sender if the player was 
	 * initialised with shouldRespond = true.
	 * @param senderId The id of the sender of the message.
	 * @param recipientId The id of the recipient of the message. Should be the same as this players id.
	 * @param message The message that was sent.
	 */
	@Override
	public final void receiveMessage(int senderId, int recipientId, String message)
	{
		System.out.println(String.format("Player %d received message from %d: %s", this.playerId, senderId, message));
		if(this.messagesSent >= MAX_MESSAGES)
		{
			if(onMessageExchangeComplete != null)
			{
				onMessageExchangeComplete.run();
			}
			return;
		}
		String newMessage = String.format("%s %d", message, this.messagesSent);
		sendMessage(senderId, newMessage);
	}

	/**
	 * Indicates that the player should stop communicating.
	 */
	@Override
	public void stopCommunication()
	{
		//Nothing to stopHub or cleanup
	}
}
