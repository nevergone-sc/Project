package cdspcore;

import java.net.InetAddress;
import java.net.InetSocketAddress;

// An Initiator defines a class of entities who is active and always initiates connections
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
			if (delegate != null && channel != null) {
				Session session = new Session(channel, delegate, ui, 1);
				session.start();
			}
		} catch (Exception e) {
			ui.printErr(e.getMessage(), "");
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