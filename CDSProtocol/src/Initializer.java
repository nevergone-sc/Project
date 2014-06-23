import java.net.*;

public abstract class Initializer implements ProtocolEntity{
	private int dstPort = 9999;
	private UserInterface ui = new ConsoleUserInterface();
	
	public void start() {
		try {
			InetAddress dstAddress = InetAddress.getByName("localhost"); 
			
			Delegate delegate = makeDelegate(ui);
			Channel channel = new UDPChannel(new InetSocketAddress(dstAddress, dstPort));
			channel.write(delegate.getInitialMessage());
			ui.print("Initial Message Sent", "Initializer");
			Session session = new Session(channel, delegate, 1);
			session.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setDstPort(int p) {
		dstPort = p;
	}
	
	protected abstract Delegate makeDelegate(UserInterface ui);
}