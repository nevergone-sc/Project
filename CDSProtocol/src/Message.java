import java.net.*;

public class Message {
	private InetAddress address;
	private int port;
	private byte[] data;
	
	public Message(InetAddress adrs, int p, byte[] d) {
		address = adrs;
		port = p;
		data = d;
	}
	
	public InetAddress getSenderAddress() {
		return address;
	}
	
	public int getSenderPort() {
		return port;
	}
	
	public byte[] getData() {
		return data;
	}
}
