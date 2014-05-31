import java.net.InetAddress;
import java.net.InetSocketAddress;


public class Accepter {
	int listeningPort;
	
	public void start() throws Exception {
		Channel mainChannel = new UDPChannel(listeningPort);
		System.out.println("Accepter Ready");
		while (true) {
			Channel subChannel = mainChannel.accept();
			System.out.println("Accepter Accepts a Connection");
			Delegate delegate = makeDelegate();
			Session session = new Session(subChannel, delegate, 0);
			session.start();
		}
	}
	
	public void setListeningPort(int port) {
		listeningPort = port;
	}
	
	protected Delegate makeDelegate() {
		return new DataReceiver();
	}
}