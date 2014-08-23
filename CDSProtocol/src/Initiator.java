import java.net.*;

public abstract class Initiator implements ProtocolEntity{
	protected String address;
	protected int dstPort;
	//private Delegate delegate;
	protected UserInterface ui = new ConsoleUserInterface();
	
	
	public Initiator(String a, int p, UserInterface ui) {
		address = a;
		dstPort = p;
		this.ui = ui;
	}
	
	public void start() {
		try {
			InetAddress dstAddress = InetAddress.getByName(address); 
			
			Delegate delegate = makeDelegate();
			Channel channel = new UDPChannel(new InetSocketAddress(dstAddress, dstPort));
			Session session = new Session(channel, delegate, ui, 1);
			session.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setDstPort(int p) {
		dstPort = p;
	}
	
	public void setUserInterface(UserInterface ui) {
		this.ui = ui;
	}
	
	protected abstract Delegate makeDelegate();
}