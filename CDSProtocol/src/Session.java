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
			receiveBuffer.flip();
			if (lengthReceived <= 0) break;
			
			lengthSent = delegate.process(receiveBuffer, sendBuffer);
			sendBuffer.flip();
			if (lengthSent <= 0) break;
			
			int actSent = channel.write(sendBuffer);
			System.out.println(lengthSent + " " + actSent);
			if (actSent != lengthSent) {
				System.out.println("Send fail: " + "length sent = " + lengthSent + " actual sent = " + actSent);
				break;
			}
		}
	}
}