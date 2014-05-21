import java.net.*;
import java.nio.ByteBuffer;


public class Initializer {
	public void start() throws Exception {
		int myPort = 8887;
		int dstPort = 8888;
		InetAddress dstAddress = InetAddress.getByName("localhost"); 
		
		Delegate delegate = makeDelegate();
		Channel channel = new UDPChannel(myPort, new InetSocketAddress(dstAddress, dstPort));
		channel.write(delegate.getInitialMessage());
		System.out.println("Initial Message Sent");
		Session session = new Session(channel, delegate, 1);
		session.start();
	}
	
	protected Delegate makeDelegate() {
		return new SendCourier();
	}
}