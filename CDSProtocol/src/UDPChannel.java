import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class UDPChannel implements Channel{
	private final int CLOSED = 0, OPEN = 1, CONNECTED = 2;
	private int state;
	private int channelPort;
	private InetSocketAddress dstAddress;
	private DatagramChannel channel;
	private ByteBuffer preMessage = null;
	
	public UDPChannel() throws Exception {
		channel = DatagramChannel.open();
		channelPort = channel.socket().getPort();
		state = OPEN;
	}
	
	public UDPChannel(int port) throws Exception {
		channelPort = port;
		channel = DatagramChannel.open();
		channel.socket().bind(new InetSocketAddress(channelPort));
		state = OPEN;
	}
	
	public UDPChannel(InetSocketAddress address) throws Exception {
		channel = DatagramChannel.open();
		channelPort = channel.socket().getPort();
		dstAddress = address;
		state = CONNECTED;
	}
	
	public UDPChannel(int port, InetSocketAddress address) throws Exception {
		channelPort = port;
		channel = DatagramChannel.open();
		channel.socket().bind(new InetSocketAddress(channelPort));
		dstAddress = address;
		state = CONNECTED;
	}
	
	public UDPChannel accept() throws Exception {
		if (state == OPEN) {
			ByteBuffer bb = ByteBuffer.allocate(1024);
			InetSocketAddress senderAddress = (InetSocketAddress) channel.receive(bb);
			bb.flip();
			UDPChannel returnChannel = new UDPChannel(senderAddress);
			returnChannel.putPreMessage(bb);
			return returnChannel;
		} else {
			return null;
		}
	}
	
	public boolean connect(InetSocketAddress address) throws Exception {
		if (state == OPEN) {
			dstAddress = address;
			state = CONNECTED;
			return true;
		} else {
			return false;
		}
	}
	
	public boolean close() throws Exception {
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
	
	public int read(ByteBuffer dst) throws Exception {
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
	
	public int write(ByteBuffer src) throws Exception {
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
