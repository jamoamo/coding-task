package task;

/**
 * Main class to start a message exchange in which all participants run in the same process.
 */
public final class InProcessMessageExchange
{
	public static void main(String[] args)
	{
		final AsyncMessageHub hub = new AsyncMessageHub();
		Player initiatingPlayer = new Player(1);
		initiatingPlayer.setMessageHub(hub);
		Player receivingPlayer = new Player(2);
		receivingPlayer.setMessageHub(hub);

		//sendMessages(initiatingPlayer);
		initiatingPlayer.initiate(() -> onComplete(hub));
	}
	
	private static void onComplete(AsyncMessageHub hub)
	{
		hub.stopHub();
	}
}
