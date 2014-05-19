import java.nio.ByteBuffer;


public class Session {
	Channel channel;
	Delegate delegate;
	
	public Session(Channel c, Delegate d) {
		channel = c;
		delegate = d;
	}
	
	public void start() throws Exception {
		ByteBuffer receiveBuffer = ByteBuffer.allocate(1024);
		ByteBuffer sendBuffer = ByteBuffer.allocate(1024);
		int lengthSent = 0;
		int lengthReceived = 0;
		
		while (true) {
			lengthReceived = channel.read(receiveBuffer);
			if (lengthReceived <= 0) break;
			
			lengthSent = delegate.process(receiveBuffer, sendBuffer);
			if (lengthSent <= 0) break;
			
			if (channel.write(sendBuffer) != lengthSent) {
				System.out.println("Send fail");
				break;
			}
		}
	}
}