import java.net.*;
import java.nio.ByteBuffer;


public class Sender {
	public void start() throws Exception {
		int myPort = 8888;
		int sendPort = 8887;
		InetAddress dstAddress = InetAddress.getByName("localhost"); 
		
		Delegate delegate = makeDelegate();
		Channel channel = new UDPChannel(myPort, new InetSocketAddress(dstAddress, sendPort));
		channel.write(delegate.getInitialMessage());
		Session session = new Session(channel, delegate);
		session.start();
	}
	
	private Delegate makeDelegate() {
		return new Courier();
	}
}
