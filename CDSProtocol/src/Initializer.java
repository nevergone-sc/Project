import java.net.*;

public abstract class Initializer {
	private int dstPort;
	
	public void start() throws Exception {		
		InetAddress dstAddress = InetAddress.getByName("localhost"); 
		
		Delegate delegate = makeDelegate();
		Channel channel = new UDPChannel(new InetSocketAddress(dstAddress, dstPort));
		channel.write(delegate.getInitialMessage());
		System.out.println("Initial Message Sent");
		Session session = new Session(channel, delegate, 1);
		session.start();
	}
	
	public void setDstPort(int p) {
		dstPort = p;
	}
	
	protected abstract Delegate makeDelegate();
}