package cdspcore;
import java.net.*;
import java.nio.ByteBuffer;

public class Message {
	private InetAddress address;
	private int port;
	private byte[] data;
	private boolean isErrorMsg = false;
	private ByteBuffer message;
	
	public Message(InetAddress adrs, int p, byte[] d) {
		address = adrs;
		port = p;
		data = d;
	}
	
	public Message(ByteBuffer src, boolean error) {
		message = src;
		isErrorMsg = error;
	}
	
	public boolean isErrorMsg() {
		return isErrorMsg;
	}
	
	public ByteBuffer getMessage() {
		return message;
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
