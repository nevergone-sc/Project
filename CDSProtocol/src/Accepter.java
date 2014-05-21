import java.net.InetAddress;
import java.net.InetSocketAddress;


public class Accepter {
	public void start() throws Exception {
		int myPort = 8888;
		int sendPort = 8887;
		
		Channel mainChannel = new UDPChannel(myPort);
		System.out.println("Accepter Ready");
		while (true) {
			Channel sideChannel = mainChannel.accept();
			System.out.println("Accepter Accepts a Connection");
			Delegate delegate = makeDelegate();
			Session session = new Session(sideChannel, delegate, 0);
			session.start();
		}
	}
	
	protected Delegate makeDelegate() {
		return new DataReceiver();
	}
}