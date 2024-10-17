package task;

/**
 * Main class to start a client message exchange. Connects to a Server message exchange to exchange messages.
 */
public final class ClientMessageExchange
{
	private ClientMessageExchange(){}
	
	public static void main(String[] args) throws Exception
	{
		AsyncMessageHub hub = new ClientMessageHub();
		if(hub.startHub())
		{
			Player receivingPlayer = new Player(2);
			receivingPlayer.setMessageHub(hub);
		}
		//Program ends when the client socket is disconnected.
	}
}
