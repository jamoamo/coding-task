package task;

/**
 * Main class to start a server message exchange. Starts a server to which client message exchanges can connect.
 */
public class ServerMessageExchange
{
	public static void main(String[] args) throws InterruptedException
	{
		AsyncMessageHub hub = new ServerMessageHub();
		if(hub.startHub())
		{
			Player initiatingPlayer = new Player(1);
			initiatingPlayer.setMessageHub(hub);
		
			initiatingPlayer.initiate(() -> hub.stopHub());
		}
	}
}
