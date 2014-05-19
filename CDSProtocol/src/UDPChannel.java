import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class UDPChannel implements Channel{
	private final int CLOSED = 0, OPEN = 1, CONNECTED = 2;
	private int state;
	private int channelPort;
	private int dstPort;
	private DatagramChannel channel;
	
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
	
	public UDPChannel(InetSocketAddress dstAddress) throws Exception {
		channel = DatagramChannel.open();
		channelPort = channel.socket().getPort();
		channel.connect(dstAddress);
		state = CONNECTED;
	}
	
	public UDPChannel(int port, InetSocketAddress dstAddress) throws Exception {
		channelPort = port;
		channel = DatagramChannel.open();
		channel.socket().bind(new InetSocketAddress(channelPort));
		channel.connect(dstAddress);
		state = CONNECTED;
	}
	
	public UDPChannel accept() throws Exception {
		if (state == OPEN) {
			ByteBuffer bb = ByteBuffer.allocate(1024);
			InetSocketAddress senderAddress = (InetSocketAddress) channel.receive(bb);
			if (bb.getInt() == 0) {
				UDPChannel returnChannel = new UDPChannel(senderAddress);
				
				bb.clear();
				bb.putInt(1);
				bb.flip();
				
				returnChannel.write(bb);				
				return returnChannel;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	public boolean connect(InetSocketAddress address) throws Exception {
		if (state == OPEN) {
			byte[] FIN = {0};
			ByteBuffer bb = ByteBuffer.allocate(1024);
			bb.put(FIN);
			bb.flip();
			channel.send(bb, address);
			
			System.out.println("message sent");
			
			bb.clear();
			InetSocketAddress senderAddress = (InetSocketAddress) channel.receive(bb);
			bb.flip();
			
			System.out.println("message received");
			if (bb.getInt() == 1) {
				channel.connect(senderAddress);
				System.out.println(senderAddress.getPort());
				state = CONNECTED;
				return true;
			} else {
				return false;
			}
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
	
	public int read(ByteBuffer dst) throws Exception {
		if (state == CONNECTED) {
			return channel.read(dst);
		} else {
			return -1;
		}
	}
	
	public int write(ByteBuffer src) throws Exception {
		if (state == CONNECTED) {
			return channel.write(src);
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
