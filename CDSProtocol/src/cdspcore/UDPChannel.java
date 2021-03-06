package cdspcore;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/* One of the implementations of Channel interface, using UDP protocol
 */
public class UDPChannel implements Channel{
	// Max payload of a UDP packet
	private static final int MAX_BUFFER_SIZE = 65535;
	private final int CLOSED = 0, OPEN = 1, CONNECTED = 2;
	private int state;
	private int channelPort;
	private InetSocketAddress dstAddress;
	private DatagramChannel channel;
	private ByteBuffer preMessage = null; // message received before this channel created
	
	public UDPChannel() throws Exception {
		channel = DatagramChannel.open();
		channelPort = channel.socket().getPort();
		state = OPEN;
	}
	
	public UDPChannel(int port) throws IOException {
		channelPort = port;
		channel = DatagramChannel.open();
		channel.socket().bind(new InetSocketAddress(channelPort));
		state = OPEN;
	}
	
	public UDPChannel(InetSocketAddress address) throws IOException {
		channel = DatagramChannel.open();
		channelPort = channel.socket().getPort();
		dstAddress = address;
		state = CONNECTED;
	}
	
	public UDPChannel(int port, InetSocketAddress address) throws IOException {
		channelPort = port;
		channel = DatagramChannel.open();
		channel.socket().bind(new InetSocketAddress(channelPort));
		dstAddress = address;
		state = CONNECTED;
	}
	
	// Accept the incoming connection, create a new channel for future communication, 
	// put previous message in to the newly created channel, return the channel. 
	public UDPChannel accept() throws IOException {
		if (state == OPEN) {
			ByteBuffer bb = ByteBuffer.allocate(MAX_BUFFER_SIZE);
			InetSocketAddress senderAddress = (InetSocketAddress) channel.receive(bb);
			bb.flip();
			UDPChannel returnChannel = new UDPChannel(senderAddress);
			returnChannel.putPreMessage(bb);
			return returnChannel;
		} else {
			return null;
		}
	}
	
	// Connect this channel to a specific address
	public boolean connect(InetSocketAddress address) throws IOException {
		if (state == OPEN) {
			dstAddress = address;
			state = CONNECTED;
			return true;
		} else {
			return false;
		}
	}
	
	public boolean close() throws IOException {
		if (state == OPEN || state == CONNECTED){
			channel.close();
			state = CLOSED;
			return true;
		} else {
			return false;
		}
	}
	
	public void putPreMessage(ByteBuffer src) {
		preMessage = src;
	}
	
	// Read from this channel and return read length if it is connected, otherwise return -1
	public int read(ByteBuffer dst) throws IOException {
		if (state == CONNECTED) {
			if (preMessage == null) {
				InetSocketAddress senderAddress = (InetSocketAddress) channel.receive(dst);
				dst.flip();
				dstAddress = senderAddress;
				return dst.limit();
			} else {
				byte[] preMessageContent = new byte[preMessage.limit()];
				preMessage.get(preMessageContent);
				dst.put(preMessageContent);
				dst.flip();
				preMessage = null;
				return dst.limit();
			}
		} else {
			return -1;
		}
	}
	
	// Write to this channel and return write length if it is connected, otherwise return -1
	public int write(ByteBuffer src) throws IOException {
		if (state == CONNECTED) {
			return channel.send(src, dstAddress);
		} else {
			return -1;
		}
	}
	
	public int getPort() {
		if (state == OPEN || state == CONNECTED) {
			return channelPort;
		} else {
			return 0;
		}
	}
}
